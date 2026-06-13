package com.example.cinema.blImpl.sales;

import com.example.cinema.bl.promotion.CouponService;
import com.example.cinema.bl.sales.PaymentService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.data.promotion.ActivityMapper;
import com.example.cinema.data.promotion.CouponMapper;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.bl.user.AccountService;
import com.example.cinema.exception.TicketAlreadyLockedException;
import com.example.cinema.po.*;
import com.example.cinema.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 票务服务实现类
 * 职责：锁座、查询、取消锁座等票务核心操作
 * 支付逻辑已迁移至 PaymentServiceImpl（降低圈复杂度）
 */
@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private ScheduleServiceForBl scheduleService;
    @Autowired
    private HallServiceForBl hallService;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private CouponService couponService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PaymentService paymentService;

    /**
     * 锁座：为用户选定的座位创建未支付的票
     */
    @Override
    public ResponseVO addTicket(TicketForm ticketForm) {
        try {
            List<SeatForm> seats = ticketForm.getSeats();
            // 批量创建未支付的票
            for (SeatForm seat : seats) {
                checkSeatAvailable(ticketForm.getScheduleId(), seat);
                insertTicket(ticketForm.getUserId(), ticketForm.getScheduleId(), seat);
            }
            // 构建返回数据
            return buildTicketWithCouponVO(ticketForm);
        } catch (TicketAlreadyLockedException e) {
            return ResponseVO.buildFailure(e.getMessage());
        } catch (DuplicateKeyException e) {
            return ResponseVO.buildFailure("该票已被抢占");
        } catch (Exception e) {
            logger.error("锁座异常: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("系统繁忙，请重试");
        }
    }

    /**
     * 获得该场次的已锁座位和场次信息
     */
    @Override
    public ResponseVO getBySchedule(int scheduleId) {
        try {
            List<Ticket> tickets = ticketMapper.selectTicketsBySchedule(scheduleId);
            ScheduleItem schedule = scheduleService.getScheduleItemById(scheduleId);
            Hall hall = hallService.getHallById(schedule.getHallId());

            int[][] seats = new int[hall.getRow()][hall.getColumn()];
            for (Ticket ticket : tickets) {
                seats[ticket.getRowIndex()][ticket.getColumnIndex()] = 1;
            }

            ScheduleWithSeatVO vo = new ScheduleWithSeatVO();
            vo.setScheduleItem(schedule);
            vo.setSeats(seats);
            return ResponseVO.buildSuccess(vo);
        } catch (Exception e) {
            logger.error("查询场次座位异常: scheduleId={}", scheduleId, e);
            return ResponseVO.buildFailure("查询场次座位失败");
        }
    }

    /**
     * 获得用户买过的票
     */
    @Override
    public ResponseVO getTicketByUser(int userId) {
        try {
            List<Ticket> tickets = ticketMapper.selectTicketByUser(userId);
            List<ShowTicketVO> toShow = buildShowTicketVOList(tickets);
            return ResponseVO.buildSuccess(toShow);
        } catch (Exception e) {
            logger.error("查询用户票务异常: userId={}", userId, e);
            return ResponseVO.buildFailure("查询用户票务失败");
        }
    }

    /**
     * 获得所有用户的票（供管理员查看）
     */
    @Override
    public ResponseVO getAllTickets() {
        try {
            List<Ticket> tickets = ticketMapper.selectAllTickets();
            List<ShowTicketVO> toShow = buildShowTicketVOList(tickets);
            // 补充用户信息
            for (int i = 0; i < tickets.size(); i++) {
                enrichWithUserInfo(toShow.get(i), tickets.get(i).getUserId());
            }
            return ResponseVO.buildSuccess(toShow);
        } catch (Exception e) {
            logger.error("查询所有票务异常: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("获取所有订单失败");
        }
    }

    /**
     * 取消锁座（只有未支付的票可以取消）
     */
    @Override
    public ResponseVO cancelTicket(List<Integer> ticketIds) {
        try {
            for (Integer ticketId : ticketIds) {
                Ticket ticket = ticketMapper.selectTicketById(ticketId);
                if (ticket != null && ticket.getState() == 0) {
                    ticketMapper.deleteTicket(ticketId);
                }
            }
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            logger.error("取消锁座异常: ticketIds={}", ticketIds, e);
            return ResponseVO.buildFailure("取消锁座失败");
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查座位是否可用
     */
    private void checkSeatAvailable(int scheduleId, SeatForm seat) {
        Ticket exist = ticketMapper.selectTicketByScheduleIdAndSeat(
                scheduleId, seat.getColumnIndex(), seat.getRowIndex());
        if (exist != null) {
            throw new TicketAlreadyLockedException();
        }
    }

    /**
     * 插入一张未支付的票
     */
    private void insertTicket(int userId, int scheduleId, SeatForm seat) {
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setScheduleId(scheduleId);
        ticket.setColumnIndex(seat.getColumnIndex());
        ticket.setRowIndex(seat.getRowIndex());
        ticket.setState(0);
        ticketMapper.insertTicket(ticket);
    }

    /**
     * 构建带优惠券信息的票务返回数据
     */
    private ResponseVO buildTicketWithCouponVO(TicketForm ticketForm) {
        int scheduleId = ticketForm.getScheduleId();
        ScheduleItem scheduleItem = scheduleService.getScheduleItemById(scheduleId);
        int movieId = scheduleItem.getMovieId();

        List<TicketVO> ticketsVO = new ArrayList<>();
        double total = 0;
        List<SeatForm> seats = ticketForm.getSeats();

        for (SeatForm seat : seats) {
            Ticket ticket = ticketMapper.selectTicketByScheduleIdAndSeat(
                    scheduleId, seat.getColumnIndex(), seat.getRowIndex());
            ticketsVO.add(ticket.getVO());
            total += scheduleItem.getFare();
        }

        List<Coupon> coupons = couponMapper.selectCouponByUser(ticketForm.getUserId());
        List<Activity> activities = activityMapper.selectActivitiesByMovie(movieId);

        TicketWithCouponVO vo = new TicketWithCouponVO();
        vo.setTicketVOList(ticketsVO);
        vo.setTotal(total);
        vo.setCoupons(coupons);
        vo.setActivities(activities);
        return ResponseVO.buildSuccess(vo);
    }

    /**
     * 将 Ticket 列表转换为 ShowTicketVO 列表
     */
    private List<ShowTicketVO> buildShowTicketVOList(List<Ticket> tickets) {
        List<ShowTicketVO> toShow = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm");

        for (Ticket ticket : tickets) {
            ScheduleItem scheduleItem = scheduleService.getScheduleItemById(ticket.getScheduleId());
            ShowTicketVO vo = new ShowTicketVO();
            vo.setTicketId(ticket.getId());
            vo.setRow(ticket.getRowIndex());
            vo.setColumn(ticket.getColumnIndex());
            vo.setUserId(ticket.getUserId());

            if (scheduleItem != null) {
                vo.setMovieName(scheduleItem.getMovieName());
                vo.setHallName(scheduleItem.getHallName());
                vo.setStartTime(formatter.format(scheduleItem.getStartTime()));
                vo.setEndTime(formatter.format(scheduleItem.getEndTime()));
            } else {
                vo.setMovieName("已删除");
                vo.setHallName("-");
                vo.setStartTime("-");
                vo.setEndTime("-");
            }

            vo.setState(mapTicketState(ticket.getState()));
            toShow.add(vo);
        }
        return toShow;
    }

    /**
     * 票状态码转可读文本
     */
    private String mapTicketState(int state) {
        switch (state) {
            case 0: return "未支付";
            case 1: return "已完成";
            case 2: return "已失效";
            default: return "未知";
        }
    }

    /**
     * 补充 ShowTicketVO 的用户名信息
     */
    private void enrichWithUserInfo(ShowTicketVO vo, int userId) {
        try {
            ResponseVO userRes = accountService.getUserById(userId);
            if (userRes != null && userRes.getSuccess() && userRes.getContent() != null) {
                Object content = userRes.getContent();
                if (content instanceof User) {
                    vo.setUsername(((User) content).getUsername());
                } else if (content instanceof UserVO) {
                    vo.setUsername(((UserVO) content).getUsername());
                } else {
                    vo.setUsername("未知用户");
                }
            } else {
                vo.setUsername("未知用户");
            }
        } catch (Exception e) {
            logger.warn("获取用户信息失败: userId={}", userId, e);
            vo.setUsername("未知用户");
        }
    }
}
