package com.example.cinema.controller.user;

import com.example.cinema.bl.user.AccountService;
import com.example.cinema.config.InterceptorConfiguration;
import com.example.cinema.vo.UserForm;
import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.cinema.po.HistoryItem;
import com.example.cinema.po.User;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 账户控制器
 * 
 * ✅ 修复：注入接口而非实现类，遵循依赖倒置原则
 * 
 * @author huwen
 * @date 2019/3/23
 */
@RestController()
public class AccountController {
    private final static String ACCOUNT_INFO_ERROR = "用户名或密码错误";
    
    @Autowired
    private AccountService accountService; // ✅ 注入接口
    
    @PostMapping("/login")
    public ResponseVO login(@RequestBody @Valid UserForm userForm, HttpSession session){
        UserVO user = accountService.login(userForm);
        if(user==null){
           return ResponseVO.buildFailure(ACCOUNT_INFO_ERROR);
        }
        // ✅ 修复：只存储UserVO，不存储明文密码
        session.setAttribute(InterceptorConfiguration.SESSION_KEY, user);
        return ResponseVO.buildSuccess(user);
    }
    
    @PostMapping("/register")
    public ResponseVO registerAccount(@RequestBody @Valid UserForm userForm){
        // ✅ 修复：强制设置kind=2，防止提权
        userForm.setKind(2);
        return accountService.registerAccount(userForm);
    }

    @PostMapping("/logout")
    public ResponseVO logOut(HttpSession session){
        session.removeAttribute(InterceptorConfiguration.SESSION_KEY);
        return ResponseVO.buildSuccess("登出成功");
    }

    @GetMapping("/get/history")
    public ResponseVO getHistory(@RequestParam int userId){
        return accountService.getHistoryByUserId(userId);
    }

    @PostMapping("/insert/history")
    public ResponseVO insertHistory(@RequestBody @Valid HistoryItem history){
        return accountService.insertHistory(history);
    }

    @GetMapping("/get/user")
    public ResponseVO getUserById(@RequestParam int userId){
        // ✅ 修复：返回UserVO而不是User PO，避免密码泄露
        return accountService.getUserVOById(userId);
    }

    @PostMapping("/update/user")
    public ResponseVO updateUser(@RequestBody @Valid User user){
        return accountService.updateUser(user);
    }

    @GetMapping("/get/all/user")
    public ResponseVO getAllUser(){
        // ✅ 修复：返回UserVO列表而不是User PO列表
        return accountService.getAllUserVO();
    }

    @PostMapping("/delete/user")
    public ResponseVO deleteUser(@RequestParam int userId){
        return accountService.deleteUser(userId);
    }
}
