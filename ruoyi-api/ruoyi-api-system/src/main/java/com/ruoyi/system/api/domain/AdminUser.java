package com.ruoyi.system.api.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-12 09:43
 **/
@Data
@ToString
@TableName("admin_users")
public class AdminUser implements Serializable {
    private static final long serialVersionUID = 1L;

        private Long id;
        private String email;
        private String username;
        private String password;
        private int active;
        private LocalDateTime confirmed_at;
}
