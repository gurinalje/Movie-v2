package com.example.cinema.po;


import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by liying on 2019/4/14.
 * 
 * ✅ 修复：使用 BigDecimal 替代 double 进行金额计算
 * 避免浮点精度问题：0.1 + 0.2 != 0.3
 */
public class VIPCard {

    // ✅ 修复：使用 BigDecimal 定义常量
    public static final BigDecimal PRICE = new BigDecimal("25.00");
    public static final BigDecimal RECHARGE_THRESHOLD = new BigDecimal("200.00");
    public static final BigDecimal RECHARGE_BONUS = new BigDecimal("30.00");

    public static final String description = "满200送30";

    /**
     * 用户id
     */
    private int userId;

    /**
     * 会员卡id
     */
    private int id;

    /**
     * 会员卡余额 ✅ 使用 BigDecimal
     */
    private BigDecimal balance;

    /**
     * 办卡日期
     */
    private Timestamp joinDate;
    
    private String name;
    
    /**
     * 累计消费金额 ✅ 使用 BigDecimal
     */
    private BigDecimal total;


    public VIPCard() {
        this.balance = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    public String getName() { return this.name; }
    public void setName(String i) { this.name = i; }
    
    public BigDecimal getTotal() { return this.total; }
    public void setTotal(BigDecimal i) { this.total = i; }
    
    // 兼容旧代码的 setter
    public void setTotal(double i) { this.total = new BigDecimal(String.valueOf(i)); }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    // 兼容旧代码的 setter
    public void setBalance(double balance) {
        this.balance = new BigDecimal(String.valueOf(balance));
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    /**
     * ✅ 修复：使用 BigDecimal 计算充值赠送金额
     * 规则：每满200送30
     * 
     * @param amount 充值金额
     * @return 充值后余额（含赠送）
     */
    public BigDecimal calculate(BigDecimal amount) {
        // 计算赠送金额：(amount / 200) * 30
        BigDecimal bonus = amount.divide(RECHARGE_THRESHOLD, 0, BigDecimal.ROUND_DOWN)
                                 .multiply(RECHARGE_BONUS);
        return amount.add(bonus);
    }
    
    // 兼容旧代码的方法重载
    public double calculate(double amount) {
        BigDecimal result = calculate(new BigDecimal(String.valueOf(amount)));
        return result.doubleValue();
    }
}
