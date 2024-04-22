package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-11 15:13
 **/
@Data
@ToString
@ApiModel("课程表")
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String register_phone;
    private String nickname;
    private String openid;
    private int sex;
    private String true_name;
    private String phone;
    private String address;
    private String verification_code;
    private Long code_expire_time;
    private Long login_count;
    private String ip;
    private Long device;
    private LocalDateTime uptime;
    private int is_del;
    private String password;
    private String avatar;




}
