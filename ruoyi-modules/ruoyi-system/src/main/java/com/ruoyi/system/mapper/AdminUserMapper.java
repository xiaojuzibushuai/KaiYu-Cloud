package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.datasource.annotation.Slave;
import com.ruoyi.system.api.domain.AdminUser;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-12 09:45
 **/
@Slave
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
