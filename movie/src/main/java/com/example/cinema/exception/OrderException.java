package com.example.cinema.exception;

/**
 * 订单异常
 */
public class OrderException extends BusinessException {

    public OrderException(String message) {
        super("ORDER_ERROR", message);
    }

    public static OrderException ticketAlreadyPaidOrExpired() {
        return new OrderException("订单异常：该票已支付或已失效");
    }

    public static OrderException emptyTicketIds() {
        return new OrderException("未传入电影票ID");
    }
}
