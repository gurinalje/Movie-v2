package com.example.cinema.blImpl.sales;

import com.example.cinema.bl.promotion.CouponService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.data.management.ScheduleMapper;
import com.example.cinema.data.sales.RefundPolicyMapper;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.data.promotion.VIPCardMapper;
import com.example.cinema.data.promotion.CouponMapper;
import com.example.cinema.data.promotion.ActivityMapper;
import com.example.cinema.po.*;
import com.example.cinema.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.*;
import java.util.*;
import com.example.cinema.bl.user.AccountService;
import java.sql.Timestamp;

// 🚀 原黑科技引入保留，避免报错
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpSession;

/**
 * Created by liying on 2019/4/16.
 * Refactored & Enhanced with Session Sniper
 */
@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    AccountService accountService;
    @Autowired
    ScheduleMapper scheduleMapper;
    @Autowired
    RefundPolicy refundPolicy;
    @Autowired
    RefundPolicyMapper refundPolicyMapper;
    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    ScheduleServiceForBl scheduleService;
    @Autowired
    HallServiceForBl hallService;
    @Autowired
    CouponMapper couponMapper;
    @Autowired
    ActivityMapper activityMapper;
    @Autowired
    VIPCardMapper vipCardMapper;
    @Autowired
    CouponService couponService;
    @Autowired
    HistoryMapper historyMapper;

    @Override
    @Transactional
    public ResponseVO addTicket(TicketForm ticketForm) {
        try{
            List<SeatForm> seats=ticketForm.getSeats();
            int numOfTickets=seats.size();
            for(int i=0;i<numOfTickets;i++){
                // 在插入前检查票是否已经存在（即已被锁座或售出）
                Ticket exist = ticketMapper.selectTicketByScheduleIdAndSeat(ticketForm.getScheduleId(), seats.get(i).getColumnIndex(), seats.get(i).getRowIndex());
                if (exist != null) {
                    return ResponseVO.buildFailure("该票已被抢占");
                }

                Ticket ticket=new Ticket();
                ticket.setUserId(ticketForm.getUserId());
                ticket.setScheduleId(ticketForm.getScheduleId());
                ticket.setColumnIndex(seats.get(i).getColumnIndex());
                ticket.setRowIndex(seats.get(i).getRowIndex());
                ticket.setState(0);
                ticketMapper.insertTicket(ticket);
            }
            TicketWithCouponVO ticketWithCouponVO=new TicketWithCouponVO();
            List<TicketVO> ticketsVOToShow=new ArrayList<>();
            int scheduleId=ticketForm.getScheduleId();
            ScheduleItem scheduleItem=scheduleService.getScheduleItemById(scheduleId);
            int movieId=scheduleItem.getMovieId();
            double total=0;
            List<Coupon> coupons=couponMapper.selectCouponByUser(ticketForm.getUserId());
            List<Activity> activities=activityMapper.selectActivitiesByMovie(movieId);
            for(int i=0;i<numOfTickets;i++){
                int rowIndex=seats.get(i).getRowIndex();
                int columnIndex=seats.get(i).getColumnIndex();
                Ticket ticketToShow=ticketMapper.selectTicketByScheduleIdAndSeat(scheduleId,columnIndex,rowIndex);
                TicketVO ticketVOToShow=ticketToShow.getVO();
                ticketsVOToShow.add(ticketVOToShow);
                total=total+scheduleItem.getFare();
            }
            ticketWithCouponVO.setTicketVOList(ticketsVOToShow);
            ticketWithCouponVO.setTotal(total);
            ticketWithCouponVO.setCoupons(coupons);
            ticketWithCouponVO.setActivities(activities);
            return ResponseVO.buildSuccess(ticketWithCouponVO);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return ResponseVO.buildFailure("该票已被抢占");
        } catch(Exception e){
            e.printStackTrace();
            if (e instanceof org.apache.ibatis.exceptions.TooManyResultsException) {
                return ResponseVO.buildFailure("该票已被抢占");
            }
            return ResponseVO.buildFailure("系统繁忙，请重试或该票已被抢占");
        }
    }

    // 🌟 核心修复 1：银行卡支付逻辑
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO completeTicket(List<Integer> id, int couponId, int explicitUserId) {
        try {
            if (id == null || id.isEmpty()) return ResponseVO.buildFailure("未传入电影票ID");
            Ticket firstTicket = ticketMapper.selectTicketById(id.get(0));
            if (firstTicket == null) return ResponseVO.buildFailure("找不到电影票信息");

            // 🚀 优化：直接使用前端传递的真实 UserID，替代无效的 Session
            int userId = explicitUserId;
            if (userId == 0) {
                userId = firstTicket.getUserId(); // 兜底方案
            }

            if (userId == 0) return ResponseVO.buildFailure("系统检测到票据所属用户为0（底层映射异常），已被安全拦截！请退出重新登录。");

            double total = 0;
            String movieName = "未知电影";
            ScheduleItem scheduleItem = scheduleService.getScheduleItemById(firstTicket.getScheduleId());
            if (scheduleItem != null) movieName = scheduleItem.getMovieName();

            // 1. 校验票的状态并计算总价
            for (Integer tId : id) {
                Ticket ticket = ticketMapper.selectTicketById(tId);
                if (ticket == null || ticket.getState() != 0) return ResponseVO.buildFailure("订单异常：该票已支付或已失效");
                ScheduleItem item = scheduleService.getScheduleItemById(ticket.getScheduleId());
                if (item != null) total += item.getFare();
            }

            // 2. 扣减优惠券
            if (couponId != 0) {
                Coupon coupon = couponMapper.selectById(couponId);
                Timestamp t = new Timestamp(System.currentTimeMillis());
                if (coupon != null && coupon.getStartTime().before(t) && coupon.getEndTime().after(t) && total >= coupon.getTargetAmount()) {
                    total -= coupon.getDiscountAmount();
                }
            }

            // 3. 更新电影票状态为已支付
            for (Integer tId : id) {
                ticketMapper.updateTicketState(tId, 1);
            }

            // 4. 完美保留原逻辑：赠送活动优惠券
            if (scheduleItem != null) {
                List<Activity> activities = activityMapper.selectActivitiesByMovie(scheduleItem.getMovieId());
                if (activities != null) {
                    for (Activity activity : activities) {
                        if (activity.getCoupon() != null) {
                            couponMapper.insertCouponUser(activity.getCoupon().getId(), userId);
                        }
                    }
                }
            }

            // 5. 🚀 强行生成账单流水，并记在正确的 UserID 头上！
            try {
                HistoryItem history = new HistoryItem();
                history.setUserId(userId);
                history.setKind(2); // 2代表消费购票
                history.setMoney(-total);
                history.setDescription("银行卡购买《" + movieName + "》");
                historyMapper.insertHistory(history);
            } catch (Exception e) { System.out.println("流水插入失败：" + e.getMessage()); }

            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("银行卡支付后端报错: " + e.getMessage());
        }
    }

    // 🌟 核心修复 2：VIP卡支付逻辑
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO completeByVIPCard(List<Integer> id, int couponId, int explicitUserId) {
        try {
            if (id == null || id.isEmpty()) return ResponseVO.buildFailure("未传入电影票ID");
            Ticket firstTicket = ticketMapper.selectTicketById(id.get(0));
            if (firstTicket == null) return ResponseVO.buildFailure("找不到电影票信息");

            // 🚀 优化：直接使用前端传递的真实 UserID，替代无效的 Session
            int userId = explicitUserId;
            if (userId == 0) {
                userId = firstTicket.getUserId(); // 兜底方案
            }

            if (userId == 0) return ResponseVO.buildFailure("系统检测到票据所属用户为0（底层映射异常），已被安全拦截！请退出重新登录。");

            double total = 0;
            String movieName = "未知电影";
            ScheduleItem scheduleItem = scheduleService.getScheduleItemById(firstTicket.getScheduleId());
            if (scheduleItem != null) movieName = scheduleItem.getMovieName();

            // 1. 校验票
            for (Integer tId : id) {
                Ticket ticket = ticketMapper.selectTicketById(tId);
                if (ticket == null || ticket.getState() != 0) return ResponseVO.buildFailure("订单异常：该票已支付或已失效");
                ScheduleItem item = scheduleService.getScheduleItemById(ticket.getScheduleId());
                if (item != null) total += item.getFare();
            }

            // 2. 扣减优惠券
            if (couponId != 0) {
                Coupon coupon = couponMapper.selectById(couponId);
                Timestamp t = new Timestamp(System.currentTimeMillis());
                if (coupon != null && coupon.getStartTime().before(t) && coupon.getEndTime().after(t) && total >= coupon.getTargetAmount()) {
                    total -= coupon.getDiscountAmount();
                }
            }

            // 3. 🛡️ 拿着真实的 UserID 去查 VIP 卡
            VIPCard vipCard = vipCardMapper.selectCardByUserId(userId);
            if (vipCard == null) return ResponseVO.buildFailure("未找到您的VIP卡！(当前检测到的UserID=" + userId + ")");
            if (vipCard.getBalance() < total) return ResponseVO.buildFailure("VIP余额不足！需要：￥" + total + "，当前余额：￥" + vipCard.getBalance());

            // 4. 扣除余额并保存
            vipCard.setBalance(vipCard.getBalance() - total);
            vipCardMapper.updateCardBalance(vipCard.getId(), vipCard.getBalance());

            // 5. 更新票状态
            for (Integer tId : id) {
                ticketMapper.updateTicketState(tId, 1);
            }

            // 6. 赠送优惠券
            if (scheduleItem != null) {
                List<Activity> activities = activityMapper.selectActivitiesByMovie(scheduleItem.getMovieId());
                if (activities != null) {
                    for (Activity activity : activities) {
                        if (activity.getCoupon() != null) {
                            couponMapper.insertCouponUser(activity.getCoupon().getId(), userId);
                        }
                    }
                }
            }

            // 7. 🚀 强行生成账单流水，并记在正确的 UserID 头上！
            try {
                HistoryItem history = new HistoryItem();
                history.setUserId(userId);
                history.setKind(2);
                history.setMoney(-total);
                history.setDescription("VIP卡购买《" + movieName + "》");
                historyMapper.insertHistory(history);
            } catch (Exception e) { System.out.println("流水插入失败：" + e.getMessage()); }

            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("后端VIP支付报错: " + e.getMessage());
        }
    }

    @Override
    public ResponseVO getBySchedule(int scheduleId) {
        // ... (保持不变)
        try {
            List<Ticket> tickets = ticketMapper.selectTicketsBySchedule(scheduleId);
            ScheduleItem schedule=scheduleService.getScheduleItemById(scheduleId);
            Hall hall=hallService.getHallById(schedule.getHallId());
            int[][] seats=new int[hall.getRow()][hall.getColumn()];
            tickets.stream().forEach(ticket -> {
                seats[ticket.getRowIndex()][ticket.getColumnIndex()]=1;
            });
            ScheduleWithSeatVO scheduleWithSeatVO=new ScheduleWithSeatVO();
            scheduleWithSeatVO.setScheduleItem(schedule);
            scheduleWithSeatVO.setSeats(seats);
            return ResponseVO.buildSuccess(scheduleWithSeatVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getTicketByUser(int userId) {
        // ... (保持不变)
        try {
            List<Ticket> tickets = ticketMapper.selectTicketByUser(userId);
            int numOfTickets=tickets.size();
            List<ShowTicketVO> toShow=new ArrayList<>();
            for(int i=0;i<numOfTickets;i++){
                ScheduleItem scheduleItem= scheduleService.getScheduleItemById(tickets.get(i).getScheduleId());
                ShowTicketVO showTicketVO=new ShowTicketVO();
                showTicketVO.setMovieName(scheduleItem.getMovieName());
                showTicketVO.setHallName(scheduleItem.getHallName());
                showTicketVO.setRow(tickets.get(i).getRowIndex());
                showTicketVO.setColumn(tickets.get(i).getColumnIndex());
                DateFormat formatter= new SimpleDateFormat("MM月dd日 HH:mm");
                String s1=formatter.format(scheduleItem.getStartTime());
                showTicketVO.setStartTime(s1);
                String s2=formatter.format(scheduleItem.getEndTime());
                showTicketVO.setEndTime(s2);
                if(tickets.get(i).getState()==0){
                    showTicketVO.setState("未支付");
                }
                else if(tickets.get(i).getState()==1){
                    showTicketVO.setState("已完成");
                }
                else{
                    showTicketVO.setState("已失效");
                }
                toShow.add(showTicketVO);
            }
            for(int i=0;i<numOfTickets;i++){
                toShow.get(i).setTicketId(tickets.get(i).getId());
            }
            return ResponseVO.buildSuccess(toShow);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getAllTickets() {
        try {
            List<Ticket> tickets = ticketMapper.selectAllTickets();
            int numOfTickets = tickets.size();
            List<ShowTicketVO> toShow = new ArrayList<>();
            for (int i = 0; i < numOfTickets; i++) {
                Ticket ticket = tickets.get(i);
                ScheduleItem scheduleItem = scheduleService.getScheduleItemById(ticket.getScheduleId());
                ShowTicketVO showTicketVO = new ShowTicketVO();
                if (scheduleItem != null) {
                    showTicketVO.setMovieName(scheduleItem.getMovieName());
                    showTicketVO.setHallName(scheduleItem.getHallName());
                    DateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm");
                    showTicketVO.setStartTime(formatter.format(scheduleItem.getStartTime()));
                    showTicketVO.setEndTime(formatter.format(scheduleItem.getEndTime()));
                } else {
                    showTicketVO.setMovieName("已删除");
                    showTicketVO.setHallName("-");
                    showTicketVO.setStartTime("-");
                    showTicketVO.setEndTime("-");
                }
                showTicketVO.setRow(ticket.getRowIndex());
                showTicketVO.setColumn(ticket.getColumnIndex());
                
                if (ticket.getState() == 0) {
                    showTicketVO.setState("未支付");
                } else if (ticket.getState() == 1) {
                    showTicketVO.setState("已完成");
                } else {
                    showTicketVO.setState("已失效");
                }
                showTicketVO.setTicketId(ticket.getId());
                
                showTicketVO.setUserId(ticket.getUserId());
                ResponseVO userRes = accountService.getUserById(ticket.getUserId());
                if (userRes != null && userRes.getSuccess() && userRes.getContent() != null) {
                    try {
                        User user = (User) userRes.getContent();
                        showTicketVO.setUsername(user.getUsername());
                    } catch (Exception e) {
                        try {
                            UserVO user = (UserVO) userRes.getContent();
                            showTicketVO.setUsername(user.getUsername());
                        } catch (Exception ignored) {
                            showTicketVO.setUsername("解析异常");
                        }
                    }
                } else {
                    showTicketVO.setUsername("未知用户");
                }
                toShow.add(showTicketVO);
            }
            return ResponseVO.buildSuccess(toShow);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("获取所有订单失败");
        }
    }

    @Override
    public ResponseVO cancelTicket(List<Integer> id) {
        // ... (保持不变)
        try{
            int numOfTickets=id.size();
            for(int i=0;i<numOfTickets;i++){
                if(ticketMapper.selectTicketById(id.get(i)).getState()==0) {
                    ticketMapper.deleteTicket(id.get(i));
                }
            }
            return ResponseVO.buildSuccess();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getRefundInfo() {
        try {
            int limit = ticketMapper.selectRefundLimit();
            return ResponseVO.buildSuccess(limit);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("获取退票策略失败");
        }
    }

    @Override
    public ResponseVO updateRefundInfo(int limitTime) {
        try {
            ticketMapper.updateRefundLimit(limitTime);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("更新退票策略失败");
        }
    }
}