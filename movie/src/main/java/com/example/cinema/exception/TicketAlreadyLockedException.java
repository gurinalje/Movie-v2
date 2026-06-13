package com.example.cinema.exception;

/**
 * 电影票已被锁定/占用异常
 */
public class TicketAlreadyLockedException extends BusinessException {

    public TicketAlreadyLockedException() {
        super("TICKET_LOCKED", "该票已被抢占");
    }

    public TicketAlreadyLockedException(String message) {
        super("TICKET_LOCKED", message);
    }
}
