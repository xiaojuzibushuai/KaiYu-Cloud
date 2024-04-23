package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.datasource.annotation.Slave;
import com.ruoyi.system.api.domain.AdminUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-12 09:45
 **/
@Slave
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    @Select("select * from admin_users where username = #{username}")
    public AdminUser getAdminUserInfo(@Param("username") String username);


}
