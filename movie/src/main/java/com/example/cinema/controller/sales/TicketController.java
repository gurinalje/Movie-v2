package com.example.cinema.controller.sales;

import com.example.cinema.bl.sales.PaymentService;
import com.example.cinema.bl.sales.RefundService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.po.HistoryItem;
import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.TicketForm;
import com.example.cinema.bl.user.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 票务控制器
 * - 锁座、查询、取消锁座 → TicketService
 * - 银行卡/VIP卡支付 → PaymentService
 * - 退票设置 → RefundService
 */
@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private AccountService accountService;

    /**
     * 锁座
     */
    @PostMapping("/lockSeat")
    public ResponseVO lockSeat(@RequestBody TicketForm ticketForm) {
        return ticketService.addTicket(ticketForm);
    }

    /**
     * 银行卡购票支付
     */
    @PostMapping("/buy")
    public ResponseVO buyTicket(@RequestParam List<Integer> ticketId,
                                @RequestParam int couponId,
                                @RequestParam(required = false, defaultValue = "0") int userId) {
        return paymentService.payByBankCard(ticketId, couponId, userId);
    }

    /**
     * VIP卡购票支付
     */
    @PostMapping("/vip/buy")
    public ResponseVO buyTicketByVIPCard(@RequestParam List<Integer> ticketId,
                                         @RequestParam int couponId,
                                         @RequestParam(required = false, defaultValue = "0") int userId) {
        return paymentService.payByVIPCard(ticketId, couponId, userId);
    }

    /**
     * 获取用户票务列表
     */
    @GetMapping("/get/{userId}")
    public ResponseVO getTicketByUserId(@PathVariable int userId) {
        return ticketService.getTicketByUser(userId);
    }

    /**
     * 获取所有票务（管理员）
     */
    @GetMapping("/get/all")
    public ResponseVO getAllTickets() {
        return ticketService.getAllTickets();
    }

    /**
     * 获取场次已占用座位
     */
    @GetMapping("/get/occupiedSeats")
    public ResponseVO getOccupiedSeats(@RequestParam int scheduleId) {
        return ticketService.getBySchedule(scheduleId);
    }

    /**
     * 取消锁座
     */
    @PostMapping("/cancel")
    public ResponseVO cancelTicket(@RequestParam List<Integer> ticketId) {
        return ticketService.cancelTicket(ticketId);
    }

    /**
     * 手动插入消费记录
     */
    @PostMapping("insert/history")
    public ResponseVO insertHistory(@RequestBody HistoryItem history) {
        return accountService.insertHistory(history);
    }

    /**
     * 获取退票时间限制
     */
    @GetMapping("/get/refundInfo")
    public ResponseVO getRefundInfo() {
        return refundService.getRefundInfo();
    }

    /**
     * 更新退票时间限制
     */
    @PostMapping("/update/refundInfo")
    public ResponseVO updateRefundInfo(@RequestParam int limitTime) {
        return refundService.updateRefundInfo(limitTime);
    }
}
