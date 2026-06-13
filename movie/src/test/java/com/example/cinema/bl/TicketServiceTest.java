package com.example.cinema.bl;

import com.example.cinema.bl.promotion.CouponService;
import com.example.cinema.bl.sales.TicketService;
import com.example.cinema.blImpl.management.hall.HallServiceForBl;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.blImpl.sales.TicketServiceImpl;
import com.example.cinema.data.management.ScheduleMapper;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TicketService 集成测试（Mock 数据库）
 * 测试 TicketServiceImpl 类的业务逻辑
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private ScheduleMapper scheduleMapper;

    @Mock
    private RefundPolicy refundPolicy;

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

    private Ticket testTicket;
    private ScheduleItem testScheduleItem;
    private Hall testHall;
    private TicketForm testTicketForm;
    private SeatForm testSeatForm;

    @Before
    public void setUp() {
        // 初始化测试数据
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
        // 测试锁座成功
        // 第一次调用返回null（表示票不存在），第二次返回testTicket（插入后查询）
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
        // 测试锁座失败（票已被抢占）
        when(ticketMapper.selectTicketByScheduleIdAndSeat(20, 5, 3)).thenReturn(testTicket);

        ResponseVO response = ticketService.addTicket(testTicketForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("该票已被抢占", response.getMessage());
    }

    @Test
    public void testAddTicketMultipleSeats() {
        // 测试多座位锁座
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

        // 每个座位第一次检查返回null，插入后查询返回testTicket
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

    // ==================== completeTicket 测试 ====================

    @Test
    public void testCompleteTicketSuccess() {
        // 测试银行卡支付成功
        List<Integer> ticketIds = Arrays.asList(1);
        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        when(scheduleService.getScheduleItemById(20)).thenReturn(testScheduleItem);
        doNothing().when(ticketMapper).updateTicketState(1, 1);
        when(activityMapper.selectActivitiesByMovie(1)).thenReturn(new ArrayList<>());
        doNothing().when(historyMapper).insertHistory(any(HistoryItem.class));

        ResponseVO response = ticketService.completeTicket(ticketIds, 0, 10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, times(1)).updateTicketState(1, 1);
    }

    @Test
    public void testCompleteTicketWithCoupon() {
        // 测试使用优惠券支付
        List<Integer> ticketIds = Arrays.asList(1);
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

        ResponseVO response = ticketService.completeTicket(ticketIds, 1, 10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    @Test
    public void testCompleteTicketEmptyIds() {
        // 测试空票ID列表
        ResponseVO response = ticketService.completeTicket(new ArrayList<>(), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("未传入电影票ID", response.getMessage());
    }

    @Test
    public void testCompleteTicketNullIds() {
        // 测试null票ID列表
        ResponseVO response = ticketService.completeTicket(null, 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("未传入电影票ID", response.getMessage());
    }

    @Test
    public void testCompleteTicketNotFound() {
        // 测试票不存在
        when(ticketMapper.selectTicketById(1)).thenReturn(null);

        ResponseVO response = ticketService.completeTicket(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("找不到电影票信息", response.getMessage());
    }

    @Test
    public void testCompleteTicketAlreadyPaid() {
        // 测试票已支付
        Ticket paidTicket = new Ticket();
        paidTicket.setId(1);
        paidTicket.setState(1);
        when(ticketMapper.selectTicketById(1)).thenReturn(paidTicket);

        ResponseVO response = ticketService.completeTicket(Arrays.asList(1), 0, 10);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("订单异常：该票已支付或已失效", response.getMessage());
    }

    // ==================== getBySchedule 测试 ====================

    @Test
    public void testGetByScheduleSuccess() {
        // 测试获取场次座位信息成功
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
        // 测试获取场次座位信息（无票）
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
        // 测试获取用户票成功
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
        // 测试获取用户票（无票）
        when(ticketMapper.selectTicketByUser(10)).thenReturn(new ArrayList<>());

        ResponseVO response = ticketService.getTicketByUser(10);

        assertNotNull(response);
        assertTrue(response.getSuccess());
    }

    // ==================== cancelTicket 测试 ====================

    @Test
    public void testCancelTicketSuccess() {
        // 测试取消锁座成功
        when(ticketMapper.selectTicketById(1)).thenReturn(testTicket);
        doNothing().when(ticketMapper).deleteTicket(1);

        ResponseVO response = ticketService.cancelTicket(Arrays.asList(1));

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, times(1)).deleteTicket(1);
    }

    @Test
    public void testCancelTicketAlreadyPaid() {
        // 测试取消已支付的票
        Ticket paidTicket = new Ticket();
        paidTicket.setId(1);
        paidTicket.setState(1);
        when(ticketMapper.selectTicketById(1)).thenReturn(paidTicket);

        ResponseVO response = ticketService.cancelTicket(Arrays.asList(1));

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, never()).deleteTicket(anyInt());
    }

    // ==================== getRefundInfo 测试 ====================

    @Test
    public void testGetRefundInfoSuccess() {
        // 测试获取退票信息成功
        when(ticketMapper.selectRefundLimit()).thenReturn(60);

        ResponseVO response = ticketService.getRefundInfo();

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(60, response.getContent());
    }

    @Test
    public void testGetRefundInfoFailure() {
        // 测试获取退票信息失败
        when(ticketMapper.selectRefundLimit()).thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = ticketService.getRefundInfo();

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("获取退票策略失败", response.getMessage());
    }

    // ==================== updateRefundInfo 测试 ====================

    @Test
    public void testUpdateRefundInfoSuccess() {
        // 测试更新退票信息成功
        doNothing().when(ticketMapper).updateRefundLimit(120);

        ResponseVO response = ticketService.updateRefundInfo(120);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(ticketMapper, times(1)).updateRefundLimit(120);
    }

    @Test
    public void testUpdateRefundInfoFailure() {
        // 测试更新退票信息失败
        doThrow(new RuntimeException("数据库错误")).when(ticketMapper).updateRefundLimit(anyInt());

        ResponseVO response = ticketService.updateRefundInfo(120);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("更新退票策略失败", response.getMessage());
    }
}
