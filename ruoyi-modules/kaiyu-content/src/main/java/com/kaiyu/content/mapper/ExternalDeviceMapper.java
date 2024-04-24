package com.kaiyu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.content.domain.ExternalDevice;
import org.apache.ibatis.annotations.Select;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-23 16:51
 **/
public interface ExternalDeviceMapper extends BaseMapper<ExternalDevice> {

    @Select("select * from external_device where deviceid = #{deviceid}")
    ExternalDevice getExternalDeviceByDeviceid(String deviceid);
}
