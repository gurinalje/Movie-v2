package com.example.cinema;

import com.example.cinema.bl.user.AccountService;
import com.example.cinema.vo.UserForm;
import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.UserVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * 账户服务单元测试
 * 测试安全修复是否正确
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CinemaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountServiceTest {
    
    @Autowired
    private AccountService accountService;
    
    @Test
    public void testRegisterWithForcedKind() {
        // 测试注册时强制设置kind=2（普通用户）
        UserForm userForm = new UserForm();
        userForm.setUsername("test_user_" + System.currentTimeMillis());
        userForm.setPassword("test123456");
        userForm.setKind(1); // 尝试注册为管理员
        
        ResponseVO response = accountService.registerAccount(userForm);
        assertTrue("注册应该成功", response.getSuccess());
        
        // 登录验证kind应该是2
        UserVO user = accountService.login(userForm);
        assertNotNull("登录应该成功", user);
        assertEquals("kind应该是2（普通用户）", Integer.valueOf(2), user.getKind());
    }
    
    @Test
    public void testGetUserVOById() {
        // 测试获取用户VO（不包含密码）
        ResponseVO response = accountService.getUserVOById(1);
        assertTrue("获取用户应该成功", response.getSuccess());
        
        UserVO userVO = (UserVO) response.getContent();
        assertNotNull("用户不应该为空", userVO);
        assertNull("UserVO不应该包含密码字段", userVO.getPassword());
    }
    
    @Test
    public void testGetAllUserVO() {
        // 测试获取所有用户VO（不包含密码）
        ResponseVO response = accountService.getAllUserVO();
        assertTrue("获取所有用户应该成功", response.getSuccess());
        
        // 验证返回的是UserVO列表而不是User列表
        Object content = response.getContent();
        assertNotNull("内容不应该为空", content);
        assertTrue("应该是列表类型", content instanceof java.util.List);
        
        java.util.List<?> userList = (java.util.List<?>) content;
        assertFalse("用户列表不应该为空", userList.isEmpty());
        
        // 验证第一个用户是UserVO类型
        Object firstUser = userList.get(0);
        assertTrue("应该是UserVO类型", firstUser instanceof UserVO);
    }
    
    @Test
    public void testLoginSuccess() {
        // 测试登录成功
        UserForm userForm = new UserForm();
        userForm.setUsername("root");
        userForm.setPassword("123456");
        
        UserVO user = accountService.login(userForm);
        assertNotNull("登录应该成功", user);
        assertEquals("用户名应该是root", "root", user.getUsername());
        assertEquals("管理员kind应该是1", Integer.valueOf(1), user.getKind());
    }
    
    @Test
    public void testLoginFailure() {
        // 测试登录失败（错误密码）
        UserForm userForm = new UserForm();
        userForm.setUsername("root");
        userForm.setPassword("wrongpassword");
        
        UserVO user = accountService.login(userForm);
        assertNull("登录应该失败", user);
    }
    
    @Test
    public void testLoginNonexistentUser() {
        // 测试登录不存在的用户
        UserForm userForm = new UserForm();
        userForm.setUsername("nonexistent_user_" + System.currentTimeMillis());
        userForm.setPassword("test123456");
        
        UserVO user = accountService.login(userForm);
        assertNull("登录应该失败", user);
    }
}