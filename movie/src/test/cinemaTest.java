package com.example.cinema;

import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.data.promotion.VIPCardMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.po.VIPCard;
import com.example.cinema.po.HistoryItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CinemaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CinemaTest {
    @Autowired
    TicketService ticketService;
    @Autowired
    HistoryMapper historyMapper;
    @Autowired
    VIPCardMapper vipCardMapper;

    @Test
    public void testVIPCardInsertAndHistory() {
        // 测试VIP卡插入和历史记录
        VIPCard vipCard = new VIPCard();
        vipCard.setUserId(7);
        vipCard.setBalance(100);
        vipCard.setName("测试用户");
        vipCard.setTotal(100);
        vipCardMapper.insertOneCard(vipCard);

        HistoryItem history = new HistoryItem();
        history.setMoney(100);
        history.setUserId(7);
        history.setKind(1);
        history.setDescription("测试充值");
        historyMapper.insertHistory(history);

        // 验证VIP卡余额与历史记录金额一致
        assertEquals(vipCardMapper.selectCardByUserId(7).getBalance(),
                     historyMapper.getHistoryByUserId(7).get(0).getMoney(), 0);
    }
}
