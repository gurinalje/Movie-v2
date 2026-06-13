package com.example.cinema.blImpl.sales;

import com.example.cinema.data.sales.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 专门处理座位释放的定时任务组件
 */
@Component
public class TicketCleanTask {

    private static final Logger logger = LoggerFactory.getLogger(TicketCleanTask.class);

    @Autowired
    private TicketMapper ticketMapper;

    // cron = "0 */1 * * * ?" 表示每隔1分钟执行一次检查
    @Scheduled(cron = "0 */1 * * * ?")
    public void autoCleanExpiredTickets() {
        try {
            ticketMapper.cleanExpiredTicket();
            logger.debug("定时任务完成：清理了15分钟未支付的座位锁");
        } catch (Exception e) {
            logger.error("定时任务异常：清理超时座位失败", e);
        }
    }
}