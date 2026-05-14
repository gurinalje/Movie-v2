package com.example.cinema.blImpl.sales;

import com.example.cinema.data.sales.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 专门处理座位释放的定时任务组件
 */
@Component
public class TicketCleanTask {

    @Autowired
    private TicketMapper ticketMapper;

    // 🚀 cron = "0 */1 * * * ?" 表示每隔1分钟执行一次检查
    // 这样不用每秒钟都去查询数据库，减轻服务器压力，且足够满足15分钟释放的需求
    @Scheduled(cron = "0 */1 * * * ?")
    public void autoCleanExpiredTickets() {
        try {
            ticketMapper.cleanExpiredTicket();
            // 如果你想看后台运行状态，可以取消下面的注释
            // System.out.println("【系统定时任务】扫描完成：清理了15分钟未支付的座位锁");
        } catch (Exception e) {
            System.out.println("【系统定时任务报错】清理超时座位失败: " + e.getMessage());
        }
    }
}