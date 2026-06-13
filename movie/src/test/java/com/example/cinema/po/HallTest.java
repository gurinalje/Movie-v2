package com.example.cinema.po;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Hall 单元测试
 * 测试 Hall 类的 getter/setter 方法
 */
public class HallTest {

    @Test
    public void testDefaultConstructor() {
        // 测试默认构造函数
        Hall hall = new Hall();
        assertNotNull(hall);
    }

    @Test
    public void testSettersAndGetters() {
        // 测试所有 setter 和 getter 方法
        Hall hall = new Hall();
        
        // 测试 id
        Integer id = 1;
        hall.setId(id);
        assertEquals(id, hall.getId());
        
        // 测试 name
        String name = "1号厅";
        hall.setName(name);
        assertEquals(name, hall.getName());
        
        // 测试 row
        Integer row = 10;
        hall.setRow(row);
        assertEquals(row, hall.getRow());
        
        // 测试 column
        Integer column = 15;
        hall.setColumn(column);
        assertEquals(column, hall.getColumn());
        
        // 测试 status
        Integer status = 1;
        hall.setStatus(status);
        assertEquals(status, hall.getStatus());
    }

    @Test
    public void testSetId() {
        // 测试 setId 方法
        Hall hall = new Hall();
        Integer id = 5;
        hall.setId(id);
        assertEquals(id, hall.getId());
    }

    @Test
    public void testSetName() {
        // 测试 setName 方法
        Hall hall = new Hall();
        String name = "VIP厅";
        hall.setName(name);
        assertEquals(name, hall.getName());
    }

    @Test
    public void testSetRow() {
        // 测试 setRow 方法
        Hall hall = new Hall();
        Integer row = 8;
        hall.setRow(row);
        assertEquals(row, hall.getRow());
    }

    @Test
    public void testSetColumn() {
        // 测试 setColumn 方法
        Hall hall = new Hall();
        Integer column = 12;
        hall.setColumn(column);
        assertEquals(column, hall.getColumn());
    }

    @Test
    public void testSetStatus() {
        // 测试 setStatus 方法
        Hall hall = new Hall();
        Integer status = 0;
        hall.setStatus(status);
        assertEquals(status, hall.getStatus());
    }

    @Test
    public void testHallWithNullValues() {
        // 测试 Hall 对象可以设置 null 值
        Hall hall = new Hall();
        
        hall.setName(null);
        assertNull(hall.getName());
    }

    @Test
    public void testHallWithZeroValues() {
        // 测试 Hall 对象可以设置 0 值
        Hall hall = new Hall();
        
        hall.setId(0);
        assertEquals(Integer.valueOf(0), hall.getId());
        
        hall.setRow(0);
        assertEquals(Integer.valueOf(0), hall.getRow());
        
        hall.setColumn(0);
        assertEquals(Integer.valueOf(0), hall.getColumn());
        
        hall.setStatus(0);
        assertEquals(Integer.valueOf(0), hall.getStatus());
    }

    @Test
    public void testHallWithNegativeValues() {
        // 测试 Hall 对象可以设置负数值
        Hall hall = new Hall();
        
        hall.setId(-1);
        assertEquals(Integer.valueOf(-1), hall.getId());
        
        hall.setRow(-5);
        assertEquals(Integer.valueOf(-5), hall.getRow());
        
        hall.setColumn(-10);
        assertEquals(Integer.valueOf(-10), hall.getColumn());
        
        hall.setStatus(-1);
        assertEquals(Integer.valueOf(-1), hall.getStatus());
    }
}
