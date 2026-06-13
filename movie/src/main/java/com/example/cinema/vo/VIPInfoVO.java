package com.example.cinema.vo;

import com.example.cinema.po.VIPStrategy;
import java.util.*;

/**
 * Created by liying on 2019/4/15.
 */
public class VIPInfoVO {

    List<String> description;

    // 🚀 核心修复：新增真实的策略对象列表，而不是仅仅传一堆描述字符串
    List<VIPStrategy> strategies;

    double price;

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<VIPStrategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<VIPStrategy> strategies) {
        this.strategies = strategies;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}