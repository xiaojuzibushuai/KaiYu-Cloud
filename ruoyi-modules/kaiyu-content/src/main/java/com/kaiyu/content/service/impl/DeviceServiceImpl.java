package com.kaiyu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.content.domain.Device;
import com.kaiyu.content.domain.DeviceGroup;
import com.kaiyu.content.domain.UserDevice;
import com.kaiyu.content.feignclient.RemoteSystemService;
import com.kaiyu.content.mapper.DeviceGroupMapper;
import com.kaiyu.content.mapper.DeviceMapper;
import com.kaiyu.content.mapper.UserDeviceMapper;
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
    private DeviceGroupMapper deviceGroupMapper;
    @Autowired
    private UserDeviceMapper userDeviceMapper;

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

    @Override
    public List getSceneList(String phone) {

        //获取用户信息
        R<UserVo> userInfo = remoteSystemService.getUserVo(phone);

        if (userInfo != null && userInfo.getData() != null && StringUtils.isNotEmpty(userInfo.getData().getUserid())) {

            List<DeviceGroup> deviceGroups = deviceGroupMapper.selectList(new LambdaQueryWrapper<DeviceGroup>()
                    .eq(DeviceGroup::getUserid, userInfo.getData().getUserid()));

            List<Map<String, Object>> dataList = new ArrayList<>();

            if (deviceGroups != null && deviceGroups.size() > 0) {

                Map<String, Object> dataDict = new HashMap<>();

                dataDict.put("sub_scenename_list", new ArrayList<>());

                for (DeviceGroup dg : deviceGroups) {
                    dataDict.put("id", dg.getId());
                    dataDict.put("userid", dg.getUserid());
                    dataDict.put("scenename", dg.getScenename());

                    if (dataList.isEmpty()) {
                        Map<String, Object> subDict = new HashMap<>();
                        subDict.put("sub_id", dg.getId());
                        subDict.put("sub_name", dg.getSubScenename());
                        ((List<Map<String, Object>>) dataDict.get("sub_scenename_list")).add(subDict);
                        dataList.add(dataDict);
                    } else {
                        boolean foundMatch = false;
                        for (Map<String, Object> da : dataList) {
                            if (da.get("scenename").equals(dg.getScenename())) {
                                Map<String, Object> subDict = new HashMap<>();
                                subDict.put("sub_id", dg.getId());
                                subDict.put("sub_name", dg.getSubScenename());
                                ((List<Map<String, Object>>) da.get("sub_scenename_list")).add(subDict);
                                foundMatch = true;
                                break;
                            }
                        }

                        if (!foundMatch) {
                            Map<String, Object> subDict = new HashMap<>();
                            subDict.put("sub_id", dg.getId());
                            subDict.put("sub_name", dg.getSubScenename());
                            ((List<Map<String, Object>>) dataDict.get("sub_scenename_list")).add(subDict);
                            dataList.add(dataDict);
                        }
                    }

                }
                return dataList;
            }
        }
        return null;
    }
}
