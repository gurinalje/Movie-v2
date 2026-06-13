package com.example.cinema.exception;

/**
 * 电影票未找到异常
 */
public class TicketNotFoundException extends BusinessException {

    public TicketNotFoundException(String message) {
        super("TICKET_NOT_FOUND", message);
    }

    public TicketNotFoundException(int ticketId) {
        super("TICKET_NOT_FOUND", "找不到电影票信息，票ID=" + ticketId);
    }
}
