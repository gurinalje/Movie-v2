package com.example.cinema.exception;

/**
 * 支付异常
 */
public class PaymentException extends BusinessException {

    public PaymentException(String message) {
        super("PAYMENT_ERROR", message);
    }

    public static PaymentException insufficientBalance(double required, double actual) {
        return new PaymentException("VIP余额不足！需要：￥" + required + "，当前余额：￥" + actual);
    }

    public static PaymentException vipCardNotFound(int userId) {
        return new PaymentException("未找到您的VIP卡！(UserID=" + userId + ")");
    }

    public static PaymentException invalidUser() {
        return new PaymentException("系统检测到票据所属用户异常，请退出重新登录");
    }
}
