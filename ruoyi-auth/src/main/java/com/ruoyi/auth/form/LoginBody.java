package com.ruoyi.auth.form;

import lombok.Data;
import lombok.ToString;

/**
 * 用户登录对象
 * 
 * @author ruoyi
 */
@Data
@ToString
public class LoginBody
{
    /**
     * 用户名
     */
    private String username;

    private String register_phone;

    private String code;

    /**
     * 用户密码
     */
    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }


    public void setsetCode(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }

}
