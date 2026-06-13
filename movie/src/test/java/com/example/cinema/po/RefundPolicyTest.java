package com.example.cinema.po;

import org.junit.Test;
import java.sql.Timestamp;
import java.util.Date;
import static org.junit.Assert.*;

/**
 * RefundPolicy 单元测试
 * 测试 RefundPolicy 类的 getter/setter 方法
 */
public class RefundPolicyTest {

    @Test
    public void testDefaultConstructor() {
        // 测试默认构造函数
        RefundPolicy policy = new RefundPolicy();
        assertNotNull(policy);
    }

    @Test
    public void testSettersAndGetters() {
        // 测试所有 setter 和 getter 方法
        RefundPolicy policy = new RefundPolicy();
        
        // 测试 id
        int id = 1;
        policy.setId(id);
        assertEquals(id, policy.getId());
        
        // 测试 timeBefore
        int timeBefore = 60;
        policy.setTimeBefore(timeBefore);
        assertEquals(timeBefore, policy.getTimeBefore());
        
        // 测试 startTime
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        policy.setStartTime(startTime);
        assertEquals(startTime, policy.getStartTime());
        
        // 测试 endTime
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + 3600000);
        policy.setEndTime(endTime);
        assertEquals(endTime, policy.getEndTime());
        
        // 测试 movieId
        int movieId = 10;
        policy.setMovieId(movieId);
        assertEquals(movieId, policy.getMovieId());
        
        // 测试 movieName
        String movieName = "测试电影";
        policy.setMovieName(movieName);
        assertEquals(movieName, policy.getMovieName());
        
        // 测试 rate
        double rate = 0.8;
        policy.setRate(rate);
        assertEquals(rate, policy.getRate(), 0.001);
    }

    @Test
    public void testSetId() {
        // 测试 setId 方法
        RefundPolicy policy = new RefundPolicy();
        int id = 5;
        policy.setId(id);
        assertEquals(id, policy.getId());
    }

    @Test
    public void testSetTimeBefore() {
        // 测试 setTimeBefore 方法
        RefundPolicy policy = new RefundPolicy();
        int timeBefore = 120;
        policy.setTimeBefore(timeBefore);
        assertEquals(timeBefore, policy.getTimeBefore());
    }

    @Test
    public void testSetStartTime() {
        // 测试 setStartTime 方法
        RefundPolicy policy = new RefundPolicy();
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        policy.setStartTime(startTime);
        assertEquals(startTime, policy.getStartTime());
    }

    @Test
    public void testSetEndTime() {
        // 测试 setEndTime 方法
        RefundPolicy policy = new RefundPolicy();
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + 7200000);
        policy.setEndTime(endTime);
        assertEquals(endTime, policy.getEndTime());
    }

    @Test
    public void testSetMovieId() {
        // 测试 setMovieId 方法
        RefundPolicy policy = new RefundPolicy();
        int movieId = 20;
        policy.setMovieId(movieId);
        assertEquals(movieId, policy.getMovieId());
    }

    @Test
    public void testSetMovieName() {
        // 测试 setMovieName 方法
        RefundPolicy policy = new RefundPolicy();
        String movieName = "新电影名称";
        policy.setMovieName(movieName);
        assertEquals(movieName, policy.getMovieName());
    }

    @Test
    public void testSetRate() {
        // 测试 setRate 方法
        RefundPolicy policy = new RefundPolicy();
        double rate = 0.5;
        policy.setRate(rate);
        assertEquals(rate, policy.getRate(), 0.001);
    }

    @Test
    public void testRefundPolicyWithZeroValues() {
        // 测试 RefundPolicy 对象可以设置 0 值
        RefundPolicy policy = new RefundPolicy();
        
        policy.setId(0);
        assertEquals(0, policy.getId());
        
        policy.setTimeBefore(0);
        assertEquals(0, policy.getTimeBefore());
        
        policy.setMovieId(0);
        assertEquals(0, policy.getMovieId());
        
        policy.setRate(0.0);
        assertEquals(0.0, policy.getRate(), 0.001);
    }

    @Test
    public void testRefundPolicyWithNegativeValues() {
        // 测试 RefundPolicy 对象可以设置负数值
        RefundPolicy policy = new RefundPolicy();
        
        policy.setId(-1);
        assertEquals(-1, policy.getId());
        
        policy.setTimeBefore(-60);
        assertEquals(-60, policy.getTimeBefore());
        
        policy.setMovieId(-10);
        assertEquals(-10, policy.getMovieId());
        
        policy.setRate(-0.5);
        assertEquals(-0.5, policy.getRate(), 0.001);
    }

    @Test
    public void testRefundPolicyWithNullValues() {
        // 测试 RefundPolicy 对象可以设置 null 值
        RefundPolicy policy = new RefundPolicy();
        
        policy.setStartTime(null);
        assertNull(policy.getStartTime());
        
        policy.setEndTime(null);
        assertNull(policy.getEndTime());
        
        policy.setMovieName(null);
        assertNull(policy.getMovieName());
    }

    @Test
    public void testRefundPolicyWithLargeValues() {
        // 测试 RefundPolicy 对象可以设置大数值
        RefundPolicy policy = new RefundPolicy();
        
        policy.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, policy.getId());
        
        policy.setTimeBefore(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, policy.getTimeBefore());
        
        policy.setMovieId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, policy.getMovieId());
        
        policy.setRate(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, policy.getRate(), 0.001);
    }
}
