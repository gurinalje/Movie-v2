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
        try {//kind是账户的类型，0、1、2分别对应老板、管理员、观众
            String encodedPassword = passwordEncoder.encode(userForm.getPassword());
            accountMapper.createNewAccount(userForm.getUsername(), encodedPassword, userForm.getKind());
        } catch (Exception e) {
            return ResponseVO.buildFailure(ACCOUNT_EXIST);
        }
        return ResponseVO.buildSuccess();
    }

    @Override
    public UserVO login(UserForm userForm) {
        User user = accountMapper.getAccountByName(userForm.getUsername());
        //System.out.println(user.getUsername()+" "+user.getKind());
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
            e.printStackTrace();
            return (ResponseVO.buildFailure("失败"));
        }
    }

    @Override
    public ResponseVO insertHistory(HistoryItem history){
        try{//System.out.println("61!");
            historyMapper.insertHistory(history);
            return ResponseVO.buildSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return (ResponseVO.buildFailure("失败"));
        }
    }

    @Override
    public ResponseVO getUserById(int id){
        try{
            User user=accountMapper.getAccountById(id);
            //System.out.println(user.getUsername()+"  "+user.getPassword());
            return ResponseVO.buildSuccess(user);
        }catch (Exception e){
            e.printStackTrace();
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
            e.printStackTrace();
            return (ResponseVO.buildFailure("失败"));
        }
    }
    @Override
    public ResponseVO getAllUser(){
        try{
            return ResponseVO.buildSuccess(accountMapper.getAllUser());
        }catch (Exception e){
            e.printStackTrace();
            return (ResponseVO.buildFailure("失败"));
        }

    }
    @Override
    public ResponseVO deleteUser(int id){
        try{
            accountMapper.deleteUser(id);
            return ResponseVO.buildSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return (ResponseVO.buildFailure("失败"));
        }
    }
}
