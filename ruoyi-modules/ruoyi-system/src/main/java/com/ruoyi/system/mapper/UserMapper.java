package com.ruoyi.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.datasource.annotation.Slave;
import com.ruoyi.system.api.domain.User;
import org.apache.ibatis.annotations.Param;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-11 16:32
 **/
@Slave
public interface UserMapper extends BaseMapper<User> {


    public User getUserInfo(@Param("register_phone") String register_phone);

    public User getUserInfoByOpenId(@Param("openId") String openId);


}
