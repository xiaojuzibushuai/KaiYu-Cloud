package com.kaiyu.content.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.content.domain.*;
import com.kaiyu.content.feignclient.RemoteSystemService;
import com.kaiyu.content.mapper.*;
import com.kaiyu.content.service.IDeviceService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.model.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

        //处理sceneid 前端传递的格式为[1,2]
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONArray.parseArray(sceneid);
        }catch (Exception e){
            KaiYuEducationException.cast(e.getMessage());
        }

        if (jsonArray == null || jsonArray.size() == 0){
            log.info("getDeviceListBySceneid时jsonArray.size() == 0");
            return null;
        }

        List<Device> result = jsonArray.stream().map(obj -> {
            String id = obj.toString();
            DeviceGroup deviceGroup = deviceGroupMapper.selectById(id);
            if (deviceGroup == null) {
                log.info("getDeviceListBySceneid时deviceGroup不存在");
                return Optional.empty();
            }

            LambdaQueryWrapper<UserDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserDevice::getSceneid, deviceGroup.getId());
            queryWrapper.eq(UserDevice::getUserid, deviceGroup.getUserid());
            List<UserDevice> userDevices = userDeviceMapper.selectList(queryWrapper);

            if (userDevices == null || userDevices.size() == 0) {
                log.info("getDeviceListBySceneid时userDevices不存在");
                return Optional.empty();
            }

            List<Device> deviceList = userDevices.stream().map(userDevice ->
                    deviceMapper.selectDeviceByDeviceId(userDevice.getDeviceid())
            ).filter(Objects::nonNull).collect(Collectors.toList());

            return Optional.of(deviceList);
        }).filter(Optional::isPresent).map(Optional::get).flatMap(list -> {
            List<Device> data = (List<Device>) list;
            return data.stream();
        }).collect(Collectors.toList());

        return result;

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

    @Override
    public List<ExternalDevice> getExternalDeviceListBySceneid(String sceneid) {

        if (StringUtils.isEmpty(sceneid)){
            log.info("getExternalDeviceListBySceneid场景id为空");
            return null;
        }

        //处理sceneid 前端传递的格式为[1,2]
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONArray.parseArray(sceneid);
        }catch (Exception e){
            KaiYuEducationException.cast(e.getMessage());
        }

        if (jsonArray == null || jsonArray.size() == 0){
            log.info("getExternalDeviceListBySceneid时jsonArray.size() == 0");
            return null;
        }

        List<ExternalDevice> result = jsonArray.stream().map(obj -> {
            String id = obj.toString();
            DeviceGroup deviceGroup = deviceGroupMapper.selectById(id);
            if (deviceGroup == null) {
                log.info("getExternalDeviceListBySceneid时deviceGroup不存在");
                return Optional.empty();
            }

            LambdaQueryWrapper<UserDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserDevice::getSceneid, deviceGroup.getId());
            queryWrapper.eq(UserDevice::getUserid, deviceGroup.getUserid());
            List<UserDevice> userDevices = userDeviceMapper.selectList(queryWrapper);

            if (userDevices.isEmpty()) {
                log.info("getDeviceListBySceneid时userDevices不存在");
                return Optional.empty();
            }

            List<ExternalDevice> collect = userDevices.stream().map(userDevice -> {
                LambdaQueryWrapper<UserExternalDevice> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(UserExternalDevice::getUserid, userDevice.getUserid());
                queryWrapper1.eq(UserExternalDevice::getExternal_deviceid, userDevice.getDeviceid());
                queryWrapper1.eq(UserExternalDevice::getDType, 2);
                return userExternalDeviceMapper.selectOne(queryWrapper1);
            }).filter(Objects::nonNull).map(userExternalDevice -> {
                return externalDeviceMapper.getExternalDeviceByDeviceid(userExternalDevice.getDeviceid());
            }).collect(Collectors.toList());

            return Optional.of(collect);

        }).filter(Optional::isPresent).map(Optional::get).flatMap(list -> {
            List<ExternalDevice> data = (List<ExternalDevice>) list;
            return data.stream();
        }).collect(Collectors.toList());

        return result;

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
                .map(this::buildExternalDeviceMap)
                .collect(Collectors.toList());
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
