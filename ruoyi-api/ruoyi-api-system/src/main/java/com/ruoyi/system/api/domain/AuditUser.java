package com.ruoyi.system.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 08:41
 **/
@Data
@ToString
@ApiModel("用户审核表")
@TableName("audit_user")
public class AuditUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("register_phone")
    private String registerPhone;
    private String nickname;
    private String openid;
    private int sex;
    @TableField("true_name")
    private String trueName;
    private String phone;
    private String address;
    @TableField("verification_code")
    private String verificationCode;
    @TableField("code_expire_time")
    private Long codeExpireTime;
    @TableField("login_count")
    private Long loginCount;
    private String ip;
    private Long device;
    private LocalDateTime uptime;
    @TableField("is_del")
    private int isDel;
    private String password;
    private String avatar;
    @TableField("role_id")
    private int roleId;

}
