package com.ruoyi.system.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-15 09:20
 **/
@Data
@ToString
public class UserVo {

    private String userid;
    private String username;
    private LocalDateTime loginTime;
    private String ipaddr;
    private String register_phone;
    private String avatar;
    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 角色列表
     */
    private Set<String> roles;

}
