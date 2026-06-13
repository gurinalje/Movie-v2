package com.example.cinema.bl;

import com.example.cinema.bl.promotion.CouponService;
import com.example.cinema.bl.sales.PaymentService;
import com.example.cinema.bl.sales.RefundService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.blImpl.sales.PaymentServiceImpl;
import com.example.cinema.blImpl.sales.RefundServiceImpl;
import com.example.cinema.blImpl.sales.TicketServiceImpl;
import com.example.cinema.data.management.ScheduleMapper;
import com.example.cinema.data.management.MovieMapper;
import com.example.cinema.data.promotion.ActivityMapper;
import com.example.cinema.data.promotion.CouponMapper;
import com.example.cinema.data.promotion.VIPCardMapper;
import com.example.cinema.data.sales.RefundPolicyMapper;
import com.example.cinema.data.sales.TicketMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.bl.user.AccountService;
import com.example.cinema.po.*;
import com.example.cinema.vo.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TicketService / PaymentService / RefundService 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

    // ==================== Mock 依赖 ====================

    @Mock
    private AccountService accountService;
    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private MovieMapper movieMapper;
    @Mock
    private RefundPolicyMapper refundPolicyMapper;
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private ScheduleServiceForBl scheduleService;
    @Mock
    private HallServiceForBl hallService;
    @Mock
    private CouponMapper couponMapper;
    @Mock
    private ActivityMapper activityMapper;
    @Mock
    private VIPCardMapper vipCardMapper;
    @Mock
    private CouponService couponService;
    @Mock
    private HistoryMapper historyMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @InjectMocks
    private RefundServiceImpl refundService;

    // ==================== 测试数据 ====================

    private Ticket testTicket;
    private ScheduleItem testScheduleItem;
    private Hall testHall;
    private TicketForm testTicketForm;
    private SeatForm testSeatForm;

    @Before
    public void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1);
        testTicket.setUserId(10);
        testTicket.setScheduleId(20);
        testTicket.setColumnIndex(5);
        testTicket.setRowIndex(3);
        testTicket.setState(0);
        testTicket.setTime(new Timestamp(System.currentTimeMillis()));

        testScheduleItem = new ScheduleItem();
        testScheduleItem.setId(20);
        testScheduleItem.setMovieId(1);
        testScheduleItem.setMovieName("测试电影");
        testScheduleItem.setHallId(1);
        testScheduleItem.setHallName("1号厅");
        testScheduleItem.setStartTime(new Date());
        testScheduleItem.setEndTime(new Date(System.currentTimeMillis() + 7200000));
        testScheduleItem.setFare(35.0);

        testHall = new Hall();
        testHall.setId(1);
        testHall.setName("1号厅");
        testHall.setRow(10);
        testHall.setColumn(15);
        testHall.setStatus(1);

        testSeatForm = new SeatForm();
        testSeatForm.setColumnIndex(5);
        testSeatForm.setRowIndex(3);

        testTicketForm = new TicketForm();
        testTicketForm.setUserId(10);
        testTicketForm.setScheduleId(20);
        testTicketForm.setSeats(Arrays.asList(testSeatForm));
    }

    // ==================== addTicket 测试 ====================

    @Test
    public void testAddTicketSuccess() {
        when(ticketMapper.selectTicketByScheduleIdAndSeat(20, 5, 3)).thenReturn(null, testTicket);
        when(ticketMapper.insertTicket(any(Ticket.class))).thenReturn(1);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(couponMapper.selectCouponByUser(10)).thenReturn(new ArrayList<>());
        when(activityMapper.selectActivitiesByMovie(1)).thenReturn(new ArrayList<>());

        ResponseVO response = ticketService.addTicket(testTicketForm);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    @Test
    public void testAddTicketAlreadyLocked() {
        when(ticketMapper.selectTicketByScheduleIdAndSeat(20, 5, 3)).thenReturn(testTicket);

        ResponseVO response = ticketService.addTicket(testTicketForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("该票已被抢占", response.getMessage());
    }

    @Test
    public void testAddTicketMultipleSeats() {
        SeatForm seat1 = new SeatForm();
        seat1.setColumnIndex(1);
        seat1.setRowIndex(1);
        SeatForm seat2 = new SeatForm();
        seat2.setColumnIndex(2);
        seat2.setRowIndex(1);

        TicketForm multiSeatForm = new TicketForm();
        multiSeatForm.setUserId(10);
        multiSeatForm.setScheduleId(20);
        multiSeatForm.setSeats(Arrays.asList(seat1, seat2));

        when(ticketMapper.selectTicketByScheduleIdAndSeat(eq(20), eq(1), eq(1))).thenReturn(null, testTicket);
        when(ticketMapper.selectTicketByScheduleIdAndSeat(eq(20), eq(2), eq(1))).thenReturn(null, testTicket);
        when(ticketMapper.insertTicket(any(Ticket.class))).thenReturn(1);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(couponMapper.selectCouponByUser(10)).thenReturn(new ArrayList<>());
        when(activityMapper.selectActivitiesByMovie(1)).thenReturn(new ArrayList<>());

        ResponseVO response = ticketService.addTicket(multiSeatForm);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    // ==================== getBySchedule 测试 ====================

    @Test
    public void testGetByScheduleSuccess() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketMapper.selectTicketsBySchedule(20)).thenReturn(tickets);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(hallService.getHallById(1)).thenReturn(testHall);

        ResponseVO response = ticketService.getBySchedule(20);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testGetByScheduleNoTickets() {
        when(ticketMapper.selectTicketsBySchedule(20)).thenReturn(new ArrayList<>());
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(hallService.getHallById(1)).thenReturn(testHall);

        ResponseVO response = ticketService.getBySchedule(20);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    // ==================== getTicketByUser 测试 ====================

    @Test
    public void testGetTicketByUserSuccess() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketMapper.selectTicketByUser(10)).thenReturn(tickets);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);

        ResponseVO response = ticketService.getTicketByUser(10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testGetTicketByUserNoTickets() {
        when(ticketMapper.selectTicketByUser(10)).thenReturn(new ArrayList<>());

        ResponseVO response = ticketService.getTicketByUser(10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    // ==================== cancelTicket 测试 ====================

    @Test
    public void testCancelTicketSuccess() {
        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        doNothing().when(ticketMapper).deleteTicket(1);

        ResponseVO response = ticketService.cancelTicket(Arrays.asList(1));

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, times(1)).deleteTicket(1);
    }

    @Test
    public void testCancelTicketAlreadyPaid() {
        Ticket paidTicket = new Ticket();
        paidTicket.setId(1);
        paidTicket.setState(1);
        when(ticketMapper.selectTicketById(1)).thenReturn(paidTicket);

        ResponseVO response = ticketService.cancelTicket(Arrays.asList(1));

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, never()).deleteTicket(anyInt());
    }

    // ==================== payByBankCard 测试 ====================

    @Test
    public void testPayByBankCardSuccess() {
        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        doNothing().when(ticketMapper).updateTicketState(1, 1);
        when(activityMapper.selectActivitiesByMovie(1)).thenReturn(new ArrayList<>());
        doNothing().when(historyMapper).insertHistory(any(HistoryItem.class));

        ResponseVO response = paymentService.payByBankCard(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, times(1)).updateTicketState(1, 1);
    }

    @Test
    public void testPayByBankCardWithCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(1);
        coupon.setStartTime(new Timestamp(System.currentTimeMillis() - 1000));
        coupon.setEndTime(new Timestamp(System.currentTimeMillis() + 3600000));
        coupon.setTargetAmount(30);
        coupon.setDiscountAmount(5);

        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(couponMapper.selectById(1)).thenReturn(coupon);
        doNothing().when(ticketMapper).updateTicketState(1, 1);
        when(activityMapper.selectActivitiesByMovie(1)).thenReturn(new ArrayList<>());
        doNothing().when(historyMapper).insertHistory(any(HistoryItem.class));

        ResponseVO response = paymentService.payByBankCard(Arrays.asList(1), 1, 10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    @Test
    public void testPayByBankCardEmptyIds() {
        ResponseVO response = paymentService.payByBankCard(new ArrayList<>(), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("未传入电影票ID", response.getMessage());
    }

    @Test
    public void testPayByBankCardNullIds() {
        ResponseVO response = paymentService.payByBankCard(null, 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("未传入电影票ID", response.getMessage());
    }

    @Test
    public void testPayByBankCardNotFound() {
        when(ticketMapper.selectTicketById(1)).thenReturn(null);

        ResponseVO response = paymentService.payByBankCard(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("找不到电影票信息", response.getMessage());
    }

    @Test
    public void testPayByBankCardAlreadyPaid() {
        Ticket paidTicket = new Ticket();
        paidTicket.setId(1);
        paidTicket.setState(1);
        when(ticketMapper.selectTicketById(1)).thenReturn(paidTicket);

        ResponseVO response = paymentService.payByBankCard(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("订单异常：该票已支付或已失效", response.getMessage());
    }

    // ==================== payByVIPCard 测试 ====================

    @Test
    public void testPayByVIPCardSuccess() {
        VIPCard vipCard = new VIPCard();
        vipCard.setId(1);
        vipCard.setUserId(10);
        vipCard.setBalance(100.0);

        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(vipCardMapper.selectCardByUserId(10)).thenReturn(vipCard);
        doNothing().when(vipCardMapper).updateCardBalance(1, 65.0);
        doNothing().when(ticketMapper).updateTicketState(1, 1);
        when(activityMapper.selectActivitiesByMovie(1)).thenReturn(new ArrayList<>());
        doNothing().when(historyMapper).insertHistory(any(HistoryItem.class));

        ResponseVO response = paymentService.payByVIPCard(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    @Test
    public void testPayByVIPCardInsufficientBalance() {
        VIPCard vipCard = new VIPCard();
        vipCard.setId(1);
        vipCard.setUserId(10);
        vipCard.setBalance(10.0);

        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(vipCardMapper.selectCardByUserId(10)).thenReturn(vipCard);

        ResponseVO response = paymentService.payByVIPCard(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertTrue(response.getMessage().contains("VIP余额不足"));
    }

    @Test
    public void testPayByVIPCardNotFound() {
        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        when(vipCardMapper.selectCardByUserId(10)).thenReturn(null);

        ResponseVO response = paymentService.payByVIPCard(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertTrue(response.getMessage().contains("未找到您的VIP卡"));
    }

    // ==================== getRefundInfo 测试 ====================

    @Test
    public void testGetRefundInfoSuccess() {
        when(ticketMapper.selectRefundLimit()).thenReturn(60);

        ResponseVO response = refundService.getRefundInfo();

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(60, response.getContent());
    }

    @Test
    public void testGetRefundInfoFailure() {
        when(ticketMapper.selectRefundLimit()).thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = refundService.getRefundInfo();

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("获取退票策略失败", response.getMessage());
    }

    // ==================== updateRefundInfo 测试 ====================

    @Test
    public void testUpdateRefundInfoSuccess() {
        doNothing().when(ticketMapper).updateRefundLimit(120);

        ResponseVO response = refundService.updateRefundInfo(120);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, times(1)).updateRefundLimit(120);
    }

    @Test
    public void testUpdateRefundInfoFailure() {
        doThrow(new RuntimeException("数据库错误")).when(ticketMapper).updateRefundLimit(anyInt());

        ResponseVO response = refundService.updateRefundInfo(120);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("更新退票策略失败", response.getMessage());
    }
}
