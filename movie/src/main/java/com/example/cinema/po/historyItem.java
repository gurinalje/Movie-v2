package com.example.cinema.po;

import org.springframework.stereotype.Component;
import java.sql.Timestamp;

@Component
public class HistoryItem {
    private int id;
    private int userId;
    private int kind;
    private double money;
    private Timestamp time;
    private String description;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getKind() { return kind; }
    public void setKind(int kind) { this.kind = kind; }

    public double getMoney() { return money; }
    public void setMoney(double money) { this.money = money; }

    public Timestamp getTime() { return time; }
    public void setTime(Timestamp time) { this.time = time; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
