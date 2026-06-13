package com.example.cinema.exception;

/**
 * 退票异常
 */
public class RefundException extends BusinessException {

    public RefundException(String message) {
        super("REFUND_ERROR", message);
    }

    public static RefundException refundFailed() {
        return new RefundException("退票失败");
    }
}
