package com.example.cinema.bl;

import com.example.cinema.bl.user.AccountService;
import com.example.cinema.blImpl.user.AccountServiceImpl;
import com.example.cinema.data.user.AccountMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.po.HistoryItem;
import com.example.cinema.po.User;
import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.UserForm;
import com.example.cinema.vo.UserVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AccountService 集成测试（Mock 数据库）
 * 测试 AccountServiceImpl 类的业务逻辑
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private HistoryMapper historyMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User testUser;
    private UserForm testUserForm;
    private HistoryItem testHistoryItem;

    @Before
    public void setUp() {
        // 初始化测试数据
        // 使用 BCrypt 编码器生成真实编码的密码
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("password123");
        
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setPassword(encodedPassword);
        testUser.setKind(0);

        testUserForm = new UserForm();
        testUserForm.setUsername("testUser");
        testUserForm.setPassword("password123");
        testUserForm.setKind(0);

        testHistoryItem = new HistoryItem();
        testHistoryItem.setUserId(1);
        testHistoryItem.setMoney(100);
        testHistoryItem.setKind(1);
        testHistoryItem.setDescription("测试充值");
    }

    @Test
    public void testRegisterAccountSuccess() {
        // 测试注册账号成功
        when(accountMapper.createNewAccount(anyString(), anyString(), anyInt())).thenReturn(1);

        ResponseVO response = accountService.registerAccount(testUserForm);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(accountMapper, times(1)).createNewAccount(anyString(), anyString(), anyInt());
    }

    @Test
    public void testRegisterAccountFailure() {
        // 测试注册账号失败（账号已存在）
        when(accountMapper.createNewAccount(anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException("账号已存在"));

        ResponseVO response = accountService.registerAccount(testUserForm);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("账号已存在", response.getMessage());
    }

    @Test
    public void testLoginSuccess() {
        // 测试登录成功
        when(accountMapper.getAccountByName("testUser")).thenReturn(testUser);

        UserVO userVO = accountService.login(testUserForm);

        assertNotNull(userVO);
        assertEquals(testUser.getId(), userVO.getId());
        assertEquals(testUser.getUsername(), userVO.getUsername());
        assertEquals(testUser.getKind(), userVO.getKind());
    }

    @Test
    public void testLoginFailureUserNotFound() {
        // 测试登录失败（用户不存在）
        when(accountMapper.getAccountByName("nonExistentUser")).thenReturn(null);

        UserForm invalidForm = new UserForm();
        invalidForm.setUsername("nonExistentUser");
        invalidForm.setPassword("password123");

        UserVO userVO = accountService.login(invalidForm);

        assertNull(userVO);
    }

    @Test
    public void testLoginFailureWrongPassword() {
        // 测试登录失败（密码错误）
        when(accountMapper.getAccountByName("testUser")).thenReturn(testUser);

        UserForm wrongPasswordForm = new UserForm();
        wrongPasswordForm.setUsername("testUser");
        wrongPasswordForm.setPassword("wrongPassword");

        UserVO userVO = accountService.login(wrongPasswordForm);

        assertNull(userVO);
    }

    @Test
    public void testGetHistoryByUserIdSuccess() {
        // 测试获取用户历史记录成功
        List<HistoryItem> historyList = Arrays.asList(testHistoryItem);
        when(historyMapper.getHistoryByUserId(1)).thenReturn(historyList);

        ResponseVO response = accountService.getHistoryByUserId(1);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testGetHistoryByUserIdFailure() {
        // 测试获取用户历史记录失败
        when(historyMapper.getHistoryByUserId(anyInt())).thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = accountService.getHistoryByUserId(1);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testInsertHistorySuccess() {
        // 测试插入历史记录成功
        doNothing().when(historyMapper).insertHistory(any(HistoryItem.class));

        ResponseVO response = accountService.insertHistory(testHistoryItem);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(historyMapper, times(1)).insertHistory(any(HistoryItem.class));
    }

    @Test
    public void testInsertHistoryFailure() {
        // 测试插入历史记录失败
        doThrow(new RuntimeException("数据库错误")).when(historyMapper).insertHistory(any(HistoryItem.class));

        ResponseVO response = accountService.insertHistory(testHistoryItem);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testGetUserByIdSuccess() {
        // 测试根据ID获取用户成功
        when(accountMapper.getAccountById(1)).thenReturn(testUser);

        ResponseVO response = accountService.getUserById(1);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
        User returnedUser = (User) response.getContent();
        assertEquals(testUser.getId(), returnedUser.getId());
        assertEquals(testUser.getUsername(), returnedUser.getUsername());
    }

    @Test
    public void testGetUserByIdFailure() {
        // 测试根据ID获取用户失败
        when(accountMapper.getAccountById(anyInt())).thenThrow(new RuntimeException("用户不存在"));

        ResponseVO response = accountService.getUserById(1);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testUpdateUserSuccess() {
        // 测试更新用户成功
        doNothing().when(accountMapper).updateUser(any(User.class));

        ResponseVO response = accountService.updateUser(testUser);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(accountMapper, times(1)).updateUser(any(User.class));
    }

    @Test
    public void testUpdateUserWithPassword() {
        // 测试更新用户密码
        doNothing().when(accountMapper).updateUser(any(User.class));

        User userWithPassword = new User();
        userWithPassword.setId(1);
        userWithPassword.setPassword("newPassword123");

        ResponseVO response = accountService.updateUser(userWithPassword);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(accountMapper, times(1)).updateUser(any(User.class));
    }

    @Test
    public void testUpdateUserFailure() {
        // 测试更新用户失败
        doThrow(new RuntimeException("数据库错误")).when(accountMapper).updateUser(any(User.class));

        ResponseVO response = accountService.updateUser(testUser);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testGetAllUserSuccess() {
        // 测试获取所有用户成功
        List<User> userList = Arrays.asList(testUser);
        when(accountMapper.getAllUser()).thenReturn(userList);

        ResponseVO response = accountService.getAllUser();

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getContent());
    }

    @Test
    public void testGetAllUserFailure() {
        // 测试获取所有用户失败
        when(accountMapper.getAllUser()).thenThrow(new RuntimeException("数据库错误"));

        ResponseVO response = accountService.getAllUser();

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }

    @Test
    public void testDeleteUserSuccess() {
        // 测试删除用户成功
        doNothing().when(accountMapper).deleteUser(anyInt());

        ResponseVO response = accountService.deleteUser(1);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(accountMapper, times(1)).deleteUser(anyInt());
    }

    @Test
    public void testDeleteUserFailure() {
        // 测试删除用户失败
        doThrow(new RuntimeException("数据库错误")).when(accountMapper).deleteUser(anyInt());

        ResponseVO response = accountService.deleteUser(1);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("失败", response.getMessage());
    }
}
