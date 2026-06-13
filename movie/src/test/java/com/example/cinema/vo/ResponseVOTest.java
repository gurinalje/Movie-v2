package com.example.cinema.vo;

import com.example.cinema.po.User;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ResponseVO 单元测试
 * 测试 ResponseVO 类的静态工厂方法和 getter/setter 方法
 */
public class ResponseVOTest {

    @Test
    public void testBuildSuccessWithoutContent() {
        // 测试无内容的成功响应
        ResponseVO response = ResponseVO.buildSuccess();
        
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNull(response.getMessage());
        assertNull(response.getContent());
    }

    @Test
    public void testBuildSuccessWithContent() {
        // 测试带内容的成功响应
        String testContent = "测试内容";
        ResponseVO response = ResponseVO.buildSuccess(testContent);
        
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNull(response.getMessage());
        assertEquals(testContent, response.getContent());
    }

    @Test
    public void testBuildSuccessWithNullContent() {
        // 测试带null内容的成功响应
        ResponseVO response = ResponseVO.buildSuccess(null);
        
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNull(response.getMessage());
        assertNull(response.getContent());
    }

    @Test
    public void testBuildFailure() {
        // 测试失败响应
        String errorMessage = "操作失败";
        ResponseVO response = ResponseVO.buildFailure(errorMessage);
        
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getContent());
    }

    @Test
    public void testBuildFailureWithEmptyMessage() {
        // 测试空错误信息的失败响应
        ResponseVO response = ResponseVO.buildFailure("");
        
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("", response.getMessage());
    }

    @Test
    public void testSettersAndGetters() {
        // 测试 setter 和 getter 方法
        ResponseVO response = new ResponseVO();
        
        response.setSuccess(true);
        assertTrue(response.getSuccess());
        
        response.setSuccess(false);
        assertFalse(response.getSuccess());
        
        String message = "测试消息";
        response.setMessage(message);
        assertEquals(message, response.getMessage());
        
        Object content = "测试内容";
        response.setContent(content);
        assertEquals(content, response.getContent());
    }

    @Test
    public void testBuildSuccessWithComplexObject() {
        // 测试复杂对象作为内容
        UserVO userVO = new UserVO(createTestUser());
        ResponseVO response = ResponseVO.buildSuccess(userVO);
        
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(userVO, response.getContent());
    }

    @Test
    public void testBuildFailurePreservesMessage() {
        // 测试失败响应保留错误信息
        String longMessage = "这是一个很长的错误信息，用于测试消息是否被正确保留";
        ResponseVO response = ResponseVO.buildFailure(longMessage);
        
        assertEquals(longMessage, response.getMessage());
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
