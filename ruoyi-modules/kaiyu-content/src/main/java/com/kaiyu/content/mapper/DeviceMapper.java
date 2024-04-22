package com.kaiyu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.content.domain.Device;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-19 15:26
 **/
public interface DeviceMapper extends BaseMapper<Device> {

    @Select("select * from device where deviceid = #{deviceId}")
    Device selectDeviceByDeviceId(@Param("deviceId") String deviceId);

}
