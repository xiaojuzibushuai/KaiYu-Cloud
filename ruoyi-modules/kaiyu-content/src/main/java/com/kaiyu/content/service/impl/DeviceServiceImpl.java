package com.kaiyu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.content.domain.Device;
import com.kaiyu.content.domain.DeviceGroup;
import com.kaiyu.content.domain.UserDevice;
import com.kaiyu.content.mapper.DeviceGroupMapper;
import com.kaiyu.content.mapper.DeviceMapper;
import com.kaiyu.content.mapper.UserDeviceMapper;
import com.kaiyu.content.service.IDeviceService;
import com.ruoyi.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-19 15:25
 **/
@Service
public class DeviceServiceImpl implements IDeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceGroupMapper deviceGroupMapper;
    @Autowired
    private UserDeviceMapper userDeviceMapper;



    @Override
    public List<Device> getDeviceListBySceneid(String sceneid) {

        if (StringUtils.isEmpty(sceneid)){
            log.info("getDeviceListBySceneid场景id为空");
            return null;
        }
        DeviceGroup deviceGroup = deviceGroupMapper.selectById(sceneid);
        if (deviceGroup == null){
            log.info("getDeviceListBySceneid时deviceGroup不存在");
            return null;
        }

        LambdaQueryWrapper<UserDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDevice::getSceneid,deviceGroup.getId());
        queryWrapper.eq(UserDevice::getUserid,deviceGroup.getUserid());
        List<UserDevice> userDevices = userDeviceMapper.selectList(queryWrapper);

        if (userDevices == null || userDevices.size() == 0){
            log.info("getDeviceListBySceneid时userDevices不存在");
            return null;
        }

        List<String> deviceids = userDevices.stream().map(userDevice -> userDevice.getDeviceid()).collect(Collectors.toList());

        List<Device> result = new ArrayList<>();

        deviceids.forEach(deviceid -> {
            Device device = deviceMapper.selectDeviceByDeviceId(deviceid);
            if (device != null){
                result.add(device);
            }
        });

        return result;

    }
}
