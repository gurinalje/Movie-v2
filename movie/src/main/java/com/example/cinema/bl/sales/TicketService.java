package com.example.cinema.bl.sales;

import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.TicketForm;

import java.util.List;

/**
 * 票务服务接口
 * 职责：锁座、查询、取消锁座等票务核心操作
 * 支付逻辑已迁移至 PaymentService
 * 退票策略已迁移至 RefundService
 */
public interface TicketService {

    /**
     * 锁座（增加票但状态为未付款）
     *
     * @param ticketForm 票务表单（含排片ID和座位列表）
     * @return 包含票务、优惠券和活动信息
     */
    ResponseVO addTicket(TicketForm ticketForm);

    /**
     * 获得该场次的已锁座位和场次信息
     *
     * @param scheduleId 排片ID
     * @return 场次座位占用情况
     */
    ResponseVO getBySchedule(int scheduleId);

    /**
     * 获得用户买过的票
     *
     * @param userId 用户ID
     * @return 用户的票务列表
     */
    ResponseVO getTicketByUser(int userId);

    /**
     * 获得所有用户的票（供管理员查看）
     *
     * @return 所有票务列表
     */
    ResponseVO getAllTickets();

    /**
     * 取消锁座（只有状态是"锁定中"的可以取消）
     *
     * @param ticketIds 票务ID列表
     * @return 操作结果
     */
    ResponseVO cancelTicket(List<Integer> ticketIds);
}
