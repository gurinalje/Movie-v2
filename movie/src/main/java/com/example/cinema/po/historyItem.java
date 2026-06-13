package com.example.cinema.po;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 历史记录实体类
 * 
 * ✅ 修复：使用 BigDecimal 替代 double 进行金额存储
 * 避免浮点精度问题
 */
@Component
public class HistoryItem {
    private int id;
    private int userId;
    private int kind;
    
    /**
     * 金额 ✅ 使用 BigDecimal
     */
    private BigDecimal money;
    
    private Timestamp time;
    private String description;

    public HistoryItem() {
        this.money = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getKind() { return kind; }
    public void setKind(int kind) { this.kind = kind; }

    public BigDecimal getMoney() { return money; }
    public void setMoney(BigDecimal money) { this.money = money; }
    
    // 兼容旧代码的 setter
    public void setMoney(double money) { 
        this.money = new BigDecimal(String.valueOf(money)); 
    }

    public Timestamp getTime() { return time; }
    public void setTime(Timestamp time) { this.time = time; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
