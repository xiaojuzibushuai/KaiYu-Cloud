package com.kaiyu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.content.domain.DeviceCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-20 09:08
 **/
public interface DeviceCategoryMapper extends BaseMapper<DeviceCategory> {

    int insertDeviceCategoryBatch(@Param("deviceCategoryList") List<DeviceCategory> deviceCategoryList);
}
