package com.example.cinema.blImpl.user;

import com.example.cinema.bl.user.AccountService;
import com.example.cinema.data.user.AccountMapper;
import com.example.cinema.data.user.HistoryMapper;
import com.example.cinema.po.User;
import com.example.cinema.po.HistoryItem;
import com.example.cinema.vo.UserForm;
import com.example.cinema.vo.ResponseVO;
import com.example.cinema.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author huwen
 * @date 2019/3/23
 */
@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final static String ACCOUNT_EXIST = "账号已存在";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private HistoryMapper historyMapper;
    
    @Override
    public ResponseVO registerAccount(UserForm userForm) {
        try {
            // ✅ 修复：强制设置kind=2（普通用户），防止提权
            String encodedPassword = passwordEncoder.encode(userForm.getPassword());
            accountMapper.createNewAccount(userForm.getUsername(), encodedPassword, 2);
        } catch (Exception e) {
            logger.error("注册失败: {}", e.getMessage(), e);
            return ResponseVO.buildFailure(ACCOUNT_EXIST);
        }
        return ResponseVO.buildSuccess();
    }

    @Override
    public UserVO login(UserForm userForm) {
        User user = accountMapper.getAccountByName(userForm.getUsername());
        if (null == user || !passwordEncoder.matches(userForm.getPassword(), user.getPassword())) {
            return null;
        }
        return new UserVO(user);
    }
    
    @Override
    public ResponseVO getHistoryByUserId(int userId){
        try{
            List<HistoryItem> list=historyMapper.getHistoryByUserId(userId);
            return(ResponseVO.buildSuccess(list));
        }
        catch (Exception e){
            logger.error("获取历史记录失败: {}", e.getMessage(), e);
            return (ResponseVO.buildFailure("失败"));
        }
    }

    @Override
    public ResponseVO insertHistory(HistoryItem history){
        try{
            historyMapper.insertHistory(history);
            return ResponseVO.buildSuccess();
        }catch (Exception e){
            logger.error("插入历史记录失败: {}", e.getMessage(), e);
            return (ResponseVO.buildFailure("失败"));
        }
    }

    @Override
    public ResponseVO getUserById(int id){
        try{
            User user=accountMapper.getAccountById(id);
            return ResponseVO.buildSuccess(user);
        }catch (Exception e){
            logger.error("获取用户失败: {}", e.getMessage(), e);
            return (ResponseVO.buildFailure("失败"));
        }
    }
    
    @Override
    public ResponseVO updateUser(User user){
        try{
            // 如果用户提供了新密码，则进行哈希加密
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            accountMapper.updateUser(user);
            return ResponseVO.buildSuccess();
        }catch (Exception e){
            logger.error("更新用户失败: {}", e.getMessage(), e);
            return (ResponseVO.buildFailure("失败"));
        }
    }
    
    @Override
    public ResponseVO getAllUser(){
        try{
            return ResponseVO.buildSuccess(accountMapper.getAllUser());
        }catch (Exception e){
            logger.error("获取所有用户失败: {}", e.getMessage(), e);
            return (ResponseVO.buildFailure("失败"));
        }
    }
    
    @Override
    public ResponseVO deleteUser(int id){
        try{
            accountMapper.deleteUser(id);
            return ResponseVO.buildSuccess();
        }catch (Exception e){
            logger.error("删除用户失败: {}", e.getMessage(), e);
            return (ResponseVO.buildFailure("失败"));
        }
    }
    
    // ✅ 新增：获取用户VO（不包含密码）
    @Override
    public ResponseVO getUserVOById(int id){
        try{
            User user = accountMapper.getAccountById(id);
            if (user == null) {
                return ResponseVO.buildFailure("用户不存在");
            }
            return ResponseVO.buildSuccess(new UserVO(user));
        }catch (Exception e){
            logger.error("获取用户VO失败: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("失败");
        }
    }
    
    // ✅ 新增：获取所有用户VO（不包含密码）
    @Override
    public ResponseVO getAllUserVO(){
        try{
            List<User> users = accountMapper.getAllUser();
            List<UserVO> userVOs = users.stream()
                    .map(UserVO::new)
                    .collect(Collectors.toList());
            return ResponseVO.buildSuccess(userVOs);
        }catch (Exception e){
            logger.error("获取所有用户VO失败: {}", e.getMessage(), e);
            return ResponseVO.buildFailure("失败");
        }
    }
}
