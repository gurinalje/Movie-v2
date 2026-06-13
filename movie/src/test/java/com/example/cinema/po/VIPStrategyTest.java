package com.example.cinema.po;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * VIPStrategy 单元测试
 * 测试 VIPStrategy 类的 getter/setter 方法和 getDescription() 方法
 */
public class VIPStrategyTest {

    @Test
    public void testDefaultConstructor() {
        // 测试默认构造函数
        VIPStrategy strategy = new VIPStrategy();
        assertNotNull(strategy);
    }

    @Test
    public void testSettersAndGetters() {
        // 测试所有 setter 和 getter 方法
        VIPStrategy strategy = new VIPStrategy();
        
        // 测试 id
        int id = 1;
        strategy.setId(id);
        assertEquals(id, strategy.getId());
        
        // 测试 chargeLimit
        int chargeLimit = 100;
        strategy.setChargeLimit(chargeLimit);
        assertEquals(chargeLimit, strategy.getChargeLimit());
        
        // 测试 giftAmount
        int giftAmount = 20;
        strategy.setGiftAmount(giftAmount);
        assertEquals(giftAmount, strategy.getGiftAmount());
    }

    @Test
    public void testSetId() {
        // 测试 setId 方法
        VIPStrategy strategy = new VIPStrategy();
        int id = 5;
        strategy.setId(id);
        assertEquals(id, strategy.getId());
    }

    @Test
    public void testSetChargeLimit() {
        // 测试 setChargeLimit 方法
        VIPStrategy strategy = new VIPStrategy();
        int chargeLimit = 200;
        strategy.setChargeLimit(chargeLimit);
        assertEquals(chargeLimit, strategy.getChargeLimit());
    }

    @Test
    public void testSetGiftAmount() {
        // 测试 setGiftAmount 方法
        VIPStrategy strategy = new VIPStrategy();
        int giftAmount = 50;
        strategy.setGiftAmount(giftAmount);
        assertEquals(giftAmount, strategy.getGiftAmount());
    }

    @Test
    public void testGetDescription() {
        // 测试 getDescription 方法
        VIPStrategy strategy = new VIPStrategy();
        strategy.setChargeLimit(100);
        strategy.setGiftAmount(20);
        
        String description = strategy.getDescription();
        assertNotNull(description);
        assertEquals("满100赠20", description);
    }

    @Test
    public void testGetDescriptionWithZeroValues() {
        // 测试 getDescription 方法，chargeLimit 和 giftAmount 为 0
        VIPStrategy strategy = new VIPStrategy();
        strategy.setChargeLimit(0);
        strategy.setGiftAmount(0);
        
        String description = strategy.getDescription();
        assertNotNull(description);
        assertEquals("满0赠0", description);
    }

    @Test
    public void testGetDescriptionWithLargeValues() {
        // 测试 getDescription 方法，chargeLimit 和 giftAmount 为大数值
        VIPStrategy strategy = new VIPStrategy();
        strategy.setChargeLimit(10000);
        strategy.setGiftAmount(5000);
        
        String description = strategy.getDescription();
        assertNotNull(description);
        assertEquals("满10000赠5000", description);
    }

    @Test
    public void testVIPStrategyWithZeroValues() {
        // 测试 VIPStrategy 对象可以设置 0 值
        VIPStrategy strategy = new VIPStrategy();
        
        strategy.setId(0);
        assertEquals(0, strategy.getId());
        
        strategy.setChargeLimit(0);
        assertEquals(0, strategy.getChargeLimit());
        
        strategy.setGiftAmount(0);
        assertEquals(0, strategy.getGiftAmount());
    }

    @Test
    public void testVIPStrategyWithNegativeValues() {
        // 测试 VIPStrategy 对象可以设置负数值
        VIPStrategy strategy = new VIPStrategy();
        
        strategy.setId(-1);
        assertEquals(-1, strategy.getId());
        
        strategy.setChargeLimit(-100);
        assertEquals(-100, strategy.getChargeLimit());
        
        strategy.setGiftAmount(-20);
        assertEquals(-20, strategy.getGiftAmount());
    }

    @Test
    public void testGetDescriptionWithNegativeValues() {
        // 测试 getDescription 方法，chargeLimit 和 giftAmount 为负数值
        VIPStrategy strategy = new VIPStrategy();
        strategy.setChargeLimit(-100);
        strategy.setGiftAmount(-20);
        
        String description = strategy.getDescription();
        assertNotNull(description);
        assertEquals("满-100赠-20", description);
    }
}
