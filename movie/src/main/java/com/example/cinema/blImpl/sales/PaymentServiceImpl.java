package com.example.cinema.blImpl.sales;

import com.example.cinema.bl.sales.PaymentService;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.data.promotion.ActivityMapper;
import com.example.cinema.data.promotion.CouponMapper;
import com.example.cinema.data.promotion.VIPCardMapper;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.exception.OrderException;
import com.example.cinema.exception.PaymentException;
import com.example.cinema.exception.TicketNotFoundException;
import com.example.cinema.po.*;
import com.example.cinema.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * 支付服务实现类
 * 从原 TicketServiceImpl 中提取的支付相关逻辑，降低圈复杂度
 * <p>
 * 职责：
 * - 银行卡支付 / VIP卡支付
 * - 优惠券扣减
 * - 活动优惠券赠送
 * - 消费流水记录
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private ScheduleServiceForBl scheduleService;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private VIPCardMapper vipCardMapper;
    @Autowired
    private HistoryMapper historyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO payByBankCard(List<Integer> ticketIds, int couponId, int userId) {
        try {
            // 0. 前置校验
            if (ticketIds == null || ticketIds.isEmpty()) {
                return ResponseVO.buildFailure("未传入电影票ID");
            }

            // 1. 校验并计算总价
            double total = calculateTotal(ticketIds);
            String movieName = resolveMovieName(ticketIds.get(0));

            // 2. 校验用户身份
            int resolvedUserId = resolveUserId(ticketIds.get(0), userId);

            // 3. 扣减优惠券
            total = applyCoupon(couponId, total);

            // 4. 更新所有票状态为已支付
            updateTicketStates(ticketIds, 1);

            // 5. 赠送活动优惠券
            giftActivityCoupons(ticketIds.get(0), resolvedUserId);

            // 6. 记录消费流水
            recordHistory(resolvedUserId, -total, "银行卡购买《" + movieName + "》");

            logger.info("银行卡支付成功: userId={}, total={}, movie={}", resolvedUserId, total, movieName);
            return ResponseVO.buildSuccess();
        } catch (OrderException | PaymentException e) {
            return ResponseVO.buildFailure(e.getMessage());
        } catch (DuplicateKeyException e) {
            logger.warn("银行卡支付重复键异常: {}", e.getMessage());
            return ResponseVO.buildFailure("该票已被抢占");
        } catch (Exception e) {
            logger.error("银行卡支付异常: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("支付失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO payByVIPCard(List<Integer> ticketIds, int couponId, int userId) {
        try {
            // 0. 前置校验
            if (ticketIds == null || ticketIds.isEmpty()) {
                return ResponseVO.buildFailure("未传入电影票ID");
            }

            // 1. 校验并计算总价
            double total = calculateTotal(ticketIds);
            String movieName = resolveMovieName(ticketIds.get(0));

            // 2. 校验用户身份
            int resolvedUserId = resolveUserId(ticketIds.get(0), userId);

            // 3. 扣减优惠券
            total = applyCoupon(couponId, total);

            // 4. 校验并扣减VIP卡余额
            deductVIPBalance(resolvedUserId, total);

            // 5. 更新所有票状态为已支付
            updateTicketStates(ticketIds, 1);

            // 6. 赠送活动优惠券
            giftActivityCoupons(ticketIds.get(0), resolvedUserId);

            // 7. 记录消费流水
            recordHistory(resolvedUserId, -total, "VIP卡购买《" + movieName + "》");

            logger.info("VIP卡支付成功: userId={}, total={}, movie={}", resolvedUserId, total, movieName);
            return ResponseVO.buildSuccess();
        } catch (OrderException | PaymentException e) {
            return ResponseVO.buildFailure(e.getMessage());
        } catch (Exception e) {
            logger.error("VIP卡支付异常: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("VIP支付失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 校验所有票的状态并计算总价
     */
    private double calculateTotal(List<Integer> ticketIds) {
        double total = 0;
        for (Integer ticketId : ticketIds) {
            Ticket ticket = ticketMapper.selectTicketById(ticketId);
            if (ticket == null) {
                throw new OrderException("找不到电影票信息");
            }
            if (ticket.getState() != 0) {
                throw OrderException.ticketAlreadyPaidOrExpired();
            }
            ScheduleItem item = scheduleService.getScheduleItemById(ticket.getScheduleId());
            if (item != null) {
                total += item.getFare();
            }
        }
        return total;
    }

    /**
     * 解析电影名称
     */
    private String resolveMovieName(int ticketId) {
        Ticket ticket = ticketMapper.selectTicketById(ticketId);
        if (ticket == null) {
            return "未知电影";
        }
        ScheduleItem scheduleItem = scheduleService.getScheduleItemById(ticket.getScheduleId());
        return (scheduleItem != null) ? scheduleItem.getMovieName() : "未知电影";
    }

    /**
     * 解析用户ID：优先使用显式传入的userId，否则从票信息获取
     */
    private int resolveUserId(int ticketId, int explicitUserId) {
        if (explicitUserId != 0) {
            return explicitUserId;
        }
        Ticket ticket = ticketMapper.selectTicketById(ticketId);
        if (ticket != null && ticket.getUserId() != 0) {
            return ticket.getUserId();
        }
        throw PaymentException.invalidUser();
    }

    /**
     * 扣减优惠券折扣
     */
    private double applyCoupon(int couponId, double total) {
        if (couponId == 0) {
            return total;
        }
        Coupon coupon = couponMapper.selectById(couponId);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (coupon != null
                && coupon.getStartTime().before(now)
                && coupon.getEndTime().after(now)
                && total >= coupon.getTargetAmount()) {
            total -= coupon.getDiscountAmount();
            logger.debug("优惠券扣减: couponId={}, discount={}", couponId, coupon.getDiscountAmount());
        }
        return total;
    }

    /**
     * 批量更新票状态
     */
    private void updateTicketStates(List<Integer> ticketIds, int state) {
        for (Integer ticketId : ticketIds) {
            ticketMapper.updateTicketState(ticketId, state);
        }
    }

    /**
     * 根据电影的活动赠送优惠券
     */
    private void giftActivityCoupons(int ticketId, int userId) {
        Ticket ticket = ticketMapper.selectTicketById(ticketId);
        if (ticket == null) {
            return;
        }
        ScheduleItem scheduleItem = scheduleService.getScheduleItemById(ticket.getScheduleId());
        if (scheduleItem == null) {
            return;
        }
        List<Activity> activities = activityMapper.selectActivitiesByMovie(scheduleItem.getMovieId());
        if (activities == null) {
            return;
        }
        for (Activity activity : activities) {
            if (activity.getCoupon() != null) {
                couponMapper.insertCouponUser(activity.getCoupon().getId(), userId);
            }
        }
    }

    /**
     * 校验并扣减VIP卡余额
     */
    private void deductVIPBalance(int userId, double total) {
        VIPCard vipCard = vipCardMapper.selectCardByUserId(userId);
        if (vipCard == null) {
            throw PaymentException.vipCardNotFound(userId);
        }
        if (vipCard.getBalance() < total) {
            throw PaymentException.insufficientBalance(total, vipCard.getBalance());
        }
        vipCard.setBalance(vipCard.getBalance() - total);
        vipCardMapper.updateCardBalance(vipCard.getId(), vipCard.getBalance());
    }

    /**
     * 记录消费流水
     */
    private void recordHistory(int userId, double money, String description) {
        try {
            HistoryItem history = new HistoryItem();
            history.setUserId(userId);
            history.setKind(2);
            history.setMoney(money);
            history.setDescription(description);
            historyMapper.insertHistory(history);
        } catch (Exception e) {
            logger.error("消费流水记录失败: userId={}, desc={}", userId, description, e);
        }
    }
}
