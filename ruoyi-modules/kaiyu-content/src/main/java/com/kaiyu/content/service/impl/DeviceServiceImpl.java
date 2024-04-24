package com.kaiyu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.content.domain.*;
import com.kaiyu.content.feignclient.RemoteSystemService;
import com.kaiyu.content.mapper.*;
import com.kaiyu.content.service.IDeviceService;
import com.ruoyi.common.core.constant.TokenConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.model.UserVo;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ExternalDeviceMapper externalDeviceMapper;
    @Autowired
    private DeviceGroupMapper deviceGroupMapper;
    @Autowired
    private UserDeviceMapper userDeviceMapper;
    @Autowired
    private UserExternalDeviceMapper userExternalDeviceMapper;

    @Autowired
    private RemoteSystemService remoteSystemService;



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

        return userDevices.stream().map(userDevice ->
                 deviceMapper.selectDeviceByDeviceId(userDevice.getDeviceid()))
                .filter(device -> device != null)
                .collect(Collectors.toList());
    }

    @Override
    public List getSceneList(String phone) {

        //获取用户信息
        R<UserVo> userInfo = remoteSystemService.getUserVo(phone);

        if (userInfo != null && userInfo.getData() != null && StringUtils.isNotEmpty(userInfo.getData().getUserid())) {

            List<DeviceGroup> deviceGroups = deviceGroupMapper.selectList(new LambdaQueryWrapper<DeviceGroup>()
                    .eq(DeviceGroup::getUserid, userInfo.getData().getUserid()));


            if (deviceGroups != null && deviceGroups.size() > 0) {

                return deviceGroups.stream().collect(
                        Collectors.groupingBy(DeviceGroup::getScenename)
                ).entrySet().stream().map(entry -> {
                    String scenename = entry.getKey();
                    List<DeviceGroup> deviceGroupsForScenename = entry.getValue();

                    DeviceGroup representativeDeviceGroup = deviceGroupsForScenename.get(0);

                    Map<String, Object> dataDict = new HashMap<>();
                    dataDict.put("id", representativeDeviceGroup.getId());
                    dataDict.put("userid", representativeDeviceGroup.getUserid());
                    dataDict.put("scenename", scenename);

                    List<Map<String, Object>> subScenes = deviceGroupsForScenename.stream()
                            .map(deviceGroup->{
                                Map<String, Object> subDict = new HashMap<>();
                                subDict.put("sub_id", deviceGroup.getId());
                                subDict.put("sub_name", deviceGroup.getSubScenename());
                                return subDict;
                            })
                            .collect(Collectors.toList());

                    dataDict.put("sub_scenename_list", subScenes);

                    return dataDict;

                }).collect(Collectors.toList());

            }
        }
        return null;
    }

    @Override
    public Map getAllDeviceListBySceneid(String sceneid) {
        DeviceGroup deviceGroup = deviceGroupMapper.selectById(sceneid);
        if (deviceGroup != null) {

            List<UserDevice> ud = userDeviceMapper.selectList(new LambdaQueryWrapper<UserDevice>()
                    .eq(UserDevice::getUserid, deviceGroup.getUserid()).eq(UserDevice::getSceneid, deviceGroup.getId()));

            List<UserExternalDevice> ued = userExternalDeviceMapper.selectList(new LambdaQueryWrapper<UserExternalDevice>()
                    .eq(UserExternalDevice::getUserid, deviceGroup.getUserid())
                    .eq(UserExternalDevice::getSceneid, deviceGroup.getId()));

            Map<String, Object> dataDict = new HashMap<>();

            dataDict.put("subScenename", deviceGroup.getSubScenename());
            dataDict.put("UserDevice", buildUserDeviceList(ud));
            dataDict.put("UserExternalDevice", buildUserExternalDeviceList(ued));

            return dataDict;

        }
        return null;
    }

    private List<Map<String, Object>> buildUserDeviceList(List<UserDevice> userDevices) {

        return userDevices.stream().map(userDevice->{
            Map<String, Object> udDict = new HashMap<>();
            udDict.put("deviceid", userDevice.getDeviceid());
            udDict.put("devicename", deviceMapper.selectDeviceByDeviceId(userDevice.getDeviceid()).getDevicename());

            List<Map<String, Object>> childList = userExternalDeviceMapper.selectList(new LambdaQueryWrapper<UserExternalDevice>()
                            .eq(UserExternalDevice::getUserid, userDevice.getUserid())
                            .eq(UserExternalDevice::getExternal_deviceid, userDevice.getDeviceid()))
                    .stream()
                    .map(this::buildExternalDeviceMap)
                    .collect(Collectors.toList());

            udDict.put("child_list", childList);
            return udDict;
        }).collect(Collectors.toList());

    }

    private List<Map<String, Object>> buildUserExternalDeviceList(List<UserExternalDevice> userExternalDevices) {
        return userExternalDevices.stream()
                .filter(ue -> ue.getExternal_deviceid() == null)
                .map(this::buildExternalDeviceMap).collect(Collectors.toList());
    }

    private Map<String, Object> buildExternalDeviceMap(UserExternalDevice userExternalDevice) {
        Map<String, Object> deviceMap = new HashMap<>();
        ExternalDevice externalDevice = externalDeviceMapper.getExternalDeviceByDeviceid(userExternalDevice.getDeviceid());
        if (externalDevice != null) {
            deviceMap.put("deviceid", externalDevice.getDeviceid());
            deviceMap.put("devicename", externalDevice.getDevicename());
            deviceMap.put("d_type", externalDevice.getDType());
        }
        return deviceMap;
    }


}
