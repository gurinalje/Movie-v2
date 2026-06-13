package com.example.cinema.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author huwen
 * @date 2019/3/23
 */
public class UserForm {
    /**
     * 用户名，不可重复
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20之间")
    private String username;
    
    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50之间")
    private String password;
    
    private int kind;

    public int getKind(){return kind;}

    public void setKind(int i){this.kind=i;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
