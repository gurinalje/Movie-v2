package com.example.cinema.blImpl.sales;

import com.example.cinema.bl.sales.RefundService;
import com.example.cinema.data.management.MovieMapper;
import com.example.cinema.data.management.ScheduleMapper;
import com.example.cinema.data.promotion.VIPCardMapper;
import com.example.cinema.data.sales.RefundPolicyMapper;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.exception.RefundException;
import com.example.cinema.po.*;
import com.example.cinema.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 退票服务实现类
 * 职责：退票操作、退票策略管理、退票时间限制设置
 */
@Service
public class RefundServiceImpl implements RefundService {

    private static final Logger logger = LoggerFactory.getLogger(RefundServiceImpl.class);

    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private RefundPolicyMapper refundPolicyMapper;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private VIPCardMapper vipCardMapper;
    @Autowired
    private HistoryMapper historyMapper;

    /**
     * 退票核心逻辑
     */
    @Override
    public ResponseVO refundTicket(int ticketId) {
        try {
            Ticket ticket = ticketMapper.selectTicketById(ticketId);
            double rate = calculateRefundRate(ticket);
            if (rate == -1.0) {
                return ResponseVO.buildSuccess(-1.0);
            }

            // 将票状态设为失效
            ticketMapper.updateTicketState(ticket.getId(), 2);

            // 计算退回金额并更新VIP余额
            double returnBalance = processRefundPayment(ticket, rate);

            // 记录退票流水
            recordRefundHistory(ticketId, ticket.getUserId(), returnBalance);

            logger.info("退票成功: ticketId={}, returnBalance={}", ticketId, returnBalance);
            return ResponseVO.buildSuccess(returnBalance);
        } catch (Exception e) {
            logger.error("退票异常: ticketId={}", ticketId, e);
            return ResponseVO.buildFailure("退票失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseVO getAllPolicy() {
        try {
            List<RefundPolicy> policies = refundPolicyMapper.getAllPolicy();
            return ResponseVO.buildSuccess(policies);
        } catch (Exception e) {
            logger.error("查询退票策略异常: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("查询退票策略失败");
        }
    }

    @Override
    public ResponseVO updateRefundPolicy(RefundPolicy refundPolicy) {
        try {
            Movie movie = movieMapper.selectMovieById(refundPolicy.getMovieId());
            refundPolicy.setMovieName(movie.getName());
            refundPolicyMapper.updateRefundPolicy(refundPolicy);
            return ResponseVO.buildSuccess("成功修改退票策略");
        } catch (Exception e) {
            logger.error("修改退票策略异常: policyId={}", refundPolicy.getId(), e);
            return ResponseVO.buildFailure("修改退票策略失败");
        }
    }

    @Override
    public ResponseVO addRefundPolicy(RefundPolicy refundPolicy) {
        try {
            Movie movie = movieMapper.selectMovieById(refundPolicy.getMovieId());
            refundPolicy.setMovieName(movie.getName());
            refundPolicyMapper.insertRefundPolicy(refundPolicy);
            return ResponseVO.buildSuccess("成功添加退票策略");
        } catch (Exception e) {
            logger.error("新增退票策略异常: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("新增退票策略失败");
        }
    }

    @Override
    public ResponseVO deleteRefundPolicy(int id) {
        try {
            refundPolicyMapper.deleteRefundPolicy(id);
            return ResponseVO.buildSuccess(refundPolicyMapper.getAllPolicy());
        } catch (Exception e) {
            logger.error("删除退票策略异常: policyId={}", id, e);
            return ResponseVO.buildFailure("删除退票策略失败");
        }
    }

    @Override
    public ResponseVO getRefundInfo() {
        try {
            int limit = ticketMapper.selectRefundLimit();
            return ResponseVO.buildSuccess(limit);
        } catch (Exception e) {
            logger.error("获取退票限制异常: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("获取退票策略失败");
        }
    }

    @Override
    public ResponseVO updateRefundInfo(int limitTime) {
        try {
            ticketMapper.updateRefundLimit(limitTime);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            logger.error("更新退票限制异常: limitTime={}", limitTime, e);
            return ResponseVO.buildFailure("更新退票策略失败");
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 计算退票费率
     *
     * @return 手续费比例，-1.0表示不可退
     */
    private double calculateRefundRate(Ticket ticket) {
        ScheduleItem scheduleItem = scheduleMapper.selectScheduleById(ticket.getScheduleId());
        int movieId = scheduleItem.getMovieId();

        int policyId = findValidRefundPolicy(movieId);
        if (policyId == 0) {
            return -1.0;
        }

        RefundPolicy policy = refundPolicyMapper.selectPolicyById(policyId);
        int validTime = policy.getTimeBefore();

        // 判断是否在退票时间限制内
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduleItem.getStartTime());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - validTime);

        Date current = new Date();
        boolean timeAllowed = current.before(calendar.getTime());
        boolean isPaid = ticket.getState() == 1;

        return (timeAllowed && isPaid) ? policy.getRate() : -1.0;
    }

    /**
     * 查找有效的退票策略
     *
     * @return 策略ID，0表示未找到
     */
    private int findValidRefundPolicy(int movieId) {
        List<RefundPolicy> list = refundPolicyMapper.getAllPolicy();
        Calendar calendar = Calendar.getInstance();
        for (RefundPolicy policy : list) {
            if (policy.getMovieId() == movieId && calendar.getTime().before(policy.getEndTime())) {
                return policy.getId();
            }
        }
        return 0;
    }

    /**
     * 处理退票退款（VIP卡余额退还）
     */
    private double processRefundPayment(Ticket ticket, double rate) {
        ScheduleItem scheduleItem = scheduleMapper.selectScheduleById(ticket.getScheduleId());
        double fare = scheduleItem.getFare();
        double returnBalance = fare * (1 - rate);

        VIPCard vipCard = vipCardMapper.selectCardByUserId(ticket.getUserId());
        if (vipCard != null) {
            double newBalance = vipCard.getBalance() + returnBalance;
            vipCardMapper.updateCardBalance(vipCard.getId(), newBalance);
        }
        return returnBalance;
    }

    /**
     * 记录退票流水
     */
    private void recordRefundHistory(int ticketId, int userId, double returnBalance) {
        try {
            HistoryItem history = new HistoryItem();
            history.setDescription("退票；金额：" + returnBalance);
            history.setUserId(userId);
            history.setKind(3);
            history.setMoney(returnBalance);
            historyMapper.insertHistory(history);
        } catch (Exception e) {
            logger.error("退票流水记录失败: ticketId={}", ticketId, e);
        }
    }
}
