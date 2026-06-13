package com.example.cinema.po;

import com.example.cinema.vo.TicketVO;
import com.example.cinema.vo.TicketWithScheduleVO;
import org.junit.Test;
import java.sql.Timestamp;
import java.util.Date;
import static org.junit.Assert.*;

/**
 * Ticket 单元测试
 * 测试 Ticket 类的 getter/setter 方法和 getVO()、getWithScheduleVO() 方法
 */
public class TicketTest {

    @Test
    public void testDefaultConstructor() {
        // 测试默认构造函数
        Ticket ticket = new Ticket();
        assertNotNull(ticket);
    }

    @Test
    public void testSettersAndGetters() {
        // 测试所有 setter 和 getter 方法
        Ticket ticket = new Ticket();
        
        // 测试 id
        int id = 1;
        ticket.setId(id);
        assertEquals(id, ticket.getId());
        
        // 测试 userId
        int userId = 10;
        ticket.setUserId(userId);
        assertEquals(userId, ticket.getUserId());
        
        // 测试 scheduleId
        int scheduleId = 20;
        ticket.setScheduleId(scheduleId);
        assertEquals(scheduleId, ticket.getScheduleId());
        
        // 测试 columnIndex
        int columnIndex = 5;
        ticket.setColumnIndex(columnIndex);
        assertEquals(columnIndex, ticket.getColumnIndex());
        
        // 测试 rowIndex
        int rowIndex = 3;
        ticket.setRowIndex(rowIndex);
        assertEquals(rowIndex, ticket.getRowIndex());
        
        // 测试 state
        int state = 1;
        ticket.setState(state);
        assertEquals(state, ticket.getState());
        
        // 测试 time
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ticket.setTime(time);
        assertEquals(time, ticket.getTime());
    }

    @Test
    public void testSetId() {
        // 测试 setId 方法
        Ticket ticket = new Ticket();
        int id = 5;
        ticket.setId(id);
        assertEquals(id, ticket.getId());
    }

    @Test
    public void testSetUserId() {
        // 测试 setUserId 方法
        Ticket ticket = new Ticket();
        int userId = 15;
        ticket.setUserId(userId);
        assertEquals(userId, ticket.getUserId());
    }

    @Test
    public void testSetScheduleId() {
        // 测试 setScheduleId 方法
        Ticket ticket = new Ticket();
        int scheduleId = 25;
        ticket.setScheduleId(scheduleId);
        assertEquals(scheduleId, ticket.getScheduleId());
    }

    @Test
    public void testSetColumnIndex() {
        // 测试 setColumnIndex 方法
        Ticket ticket = new Ticket();
        int columnIndex = 7;
        ticket.setColumnIndex(columnIndex);
        assertEquals(columnIndex, ticket.getColumnIndex());
    }

    @Test
    public void testSetRowIndex() {
        // 测试 setRowIndex 方法
        Ticket ticket = new Ticket();
        int rowIndex = 4;
        ticket.setRowIndex(rowIndex);
        assertEquals(rowIndex, ticket.getRowIndex());
    }

    @Test
    public void testSetState() {
        // 测试 setState 方法
        Ticket ticket = new Ticket();
        int state = 2;
        ticket.setState(state);
        assertEquals(state, ticket.getState());
    }

    @Test
    public void testSetTime() {
        // 测试 setTime 方法
        Ticket ticket = new Ticket();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ticket.setTime(time);
        assertEquals(time, ticket.getTime());
    }

    @Test
    public void testGetVOWithState0() {
        // 测试 state=0 时的 getVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(0);
        
        TicketVO vo = ticket.getVO();
        assertNotNull(vo);
        assertEquals(ticket.getId(), vo.getId());
        assertEquals(ticket.getUserId(), vo.getUserId());
        assertEquals(ticket.getScheduleId(), vo.getScheduleId());
        assertEquals(ticket.getRowIndex(), vo.getRowIndex());
        assertEquals(ticket.getColumnIndex(), vo.getColumnIndex());
        assertEquals("未完成", vo.getState());
        assertEquals(ticket.getTime(), vo.getTime());
    }

    @Test
    public void testGetVOWithState1() {
        // 测试 state=1 时的 getVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(1);
        
        TicketVO vo = ticket.getVO();
        assertNotNull(vo);
        assertEquals("已完成", vo.getState());
    }

    @Test
    public void testGetVOWithState2() {
        // 测试 state=2 时的 getVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(2);
        
        TicketVO vo = ticket.getVO();
        assertNotNull(vo);
        assertEquals("已失效", vo.getState());
    }

    @Test
    public void testGetVOWithInvalidState() {
        // 测试无效 state 时的 getVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(3); // 无效状态
        
        TicketVO vo = ticket.getVO();
        assertNotNull(vo);
        assertEquals("未完成", vo.getState()); // 默认返回"未完成"
    }

    @Test
    public void testGetWithScheduleVOWithState0() {
        // 测试 state=0 时的 getWithScheduleVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(0);
        
        TicketWithScheduleVO vo = ticket.getWithScheduleVO();
        assertNotNull(vo);
        assertEquals(ticket.getId(), vo.getId());
        assertEquals(ticket.getUserId(), vo.getUserId());
        assertEquals(ticket.getRowIndex(), vo.getRowIndex());
        assertEquals(ticket.getColumnIndex(), vo.getColumnIndex());
        assertEquals("未完成", vo.getState());
    }

    @Test
    public void testGetWithScheduleVOWithState1() {
        // 测试 state=1 时的 getWithScheduleVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(1);
        
        TicketWithScheduleVO vo = ticket.getWithScheduleVO();
        assertNotNull(vo);
        assertEquals("已完成", vo.getState());
    }

    @Test
    public void testGetWithScheduleVOWithState2() {
        // 测试 state=2 时的 getWithScheduleVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(2);
        
        TicketWithScheduleVO vo = ticket.getWithScheduleVO();
        assertNotNull(vo);
        assertEquals("已失效", vo.getState());
    }

    @Test
    public void testGetWithScheduleVOWithInvalidState() {
        // 测试无效 state 时的 getWithScheduleVO() 方法
        Ticket ticket = createTestTicket();
        ticket.setState(3); // 无效状态
        
        TicketWithScheduleVO vo = ticket.getWithScheduleVO();
        assertNotNull(vo);
        assertEquals("未完成", vo.getState()); // 默认返回"未完成"
    }

    @Test
    public void testTicketWithZeroValues() {
        // 测试 Ticket 对象可以设置 0 值
        Ticket ticket = new Ticket();
        
        ticket.setId(0);
        assertEquals(0, ticket.getId());
        
        ticket.setUserId(0);
        assertEquals(0, ticket.getUserId());
        
        ticket.setScheduleId(0);
        assertEquals(0, ticket.getScheduleId());
        
        ticket.setColumnIndex(0);
        assertEquals(0, ticket.getColumnIndex());
        
        ticket.setRowIndex(0);
        assertEquals(0, ticket.getRowIndex());
        
        ticket.setState(0);
        assertEquals(0, ticket.getState());
    }

    @Test
    public void testTicketWithNegativeValues() {
        // 测试 Ticket 对象可以设置负数值
        Ticket ticket = new Ticket();
        
        ticket.setId(-1);
        assertEquals(-1, ticket.getId());
        
        ticket.setUserId(-5);
        assertEquals(-5, ticket.getUserId());
        
        ticket.setScheduleId(-10);
        assertEquals(-10, ticket.getScheduleId());
        
        ticket.setColumnIndex(-3);
        assertEquals(-3, ticket.getColumnIndex());
        
        ticket.setRowIndex(-2);
        assertEquals(-2, ticket.getRowIndex());
        
        ticket.setState(-1);
        assertEquals(-1, ticket.getState());
    }

    private Ticket createTestTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setUserId(10);
        ticket.setScheduleId(20);
        ticket.setColumnIndex(5);
        ticket.setRowIndex(3);
        ticket.setState(0);
        ticket.setTime(new Timestamp(System.currentTimeMillis()));
        return ticket;
    }
}
