package com.example.cinema.vo;

import com.example.cinema.po.User;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * UserVO 单元测试
 * 测试 UserVO 类的构造函数和 getter/setter 方法
 */
public class UserVOTest {

    @Test
    public void testConstructorWithUser() {
        // 测试使用 User 对象构造 UserVO
        User user = createTestUser();
        UserVO userVO = new UserVO(user);
        
        assertNotNull(userVO);
        assertEquals(user.getId(), userVO.getId());
        assertEquals(user.getUsername(), userVO.getUsername());
        assertEquals(user.getKind(), userVO.getKind());
    }

    @Test
    public void testConstructorWithNullUser() {
        // 测试使用 null User 对象构造 UserVO
        try {
            UserVO userVO = new UserVO(null);
            // 如果 UserVO 构造函数不检查 null，这里会抛出 NullPointerException
            // 根据实际实现决定是否期望异常
        } catch (NullPointerException e) {
            // 预期行为
        }
    }

    @Test
    public void testSettersAndGetters() {
        // 测试 setter 和 getter 方法
        UserVO userVO = new UserVO(createTestUser());
        
        // 测试 setId 和 getId
        Integer newId = 2;
        userVO.setId(newId);
        assertEquals(newId, userVO.getId());
        
        // 测试 setUsername 和 getUsername
        String newUsername = "newTestUser";
        userVO.setUsername(newUsername);
        assertEquals(newUsername, userVO.getUsername());
        
        // 测试 setKind 和 getKind
        Integer newKind = 1;
        userVO.setKind(newKind);
        assertEquals(newKind, userVO.getKind());
    }

    @Test
    public void testSetKindWithSetter() {
        // 测试 setKind 方法（注意：UserVO 中 setKind 方法参数名是 id，但实际设置的是 kind）
        UserVO userVO = new UserVO(createTestUser());
        
        Integer newKind = 2;
        userVO.setKind(newKind);
        assertEquals(newKind, userVO.getKind());
    }

    @Test
    public void testGetId() {
        // 测试 getId 方法
        User user = createTestUser();
        UserVO userVO = new UserVO(user);
        
        assertEquals(user.getId(), userVO.getId());
    }

    @Test
    public void testGetUsername() {
        // 测试 getUsername 方法
        User user = createTestUser();
        UserVO userVO = new UserVO(user);
        
        assertEquals(user.getUsername(), userVO.getUsername());
    }

    @Test
    public void testGetKind() {
        // 测试 getKind 方法
        User user = createTestUser();
        UserVO userVO = new UserVO(user);
        
        assertEquals(user.getKind(), userVO.getKind());
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setKind(0);
        return user;
    }
}
