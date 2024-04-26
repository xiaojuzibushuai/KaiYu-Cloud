package com.kaiyu.content.controller;

import com.kaiyu.content.domain.Device;
import com.kaiyu.content.domain.DeviceGroup;
import com.kaiyu.content.domain.ExternalDevice;
import com.kaiyu.content.domain.RestResponse;
import com.kaiyu.content.service.IDeviceService;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import com.ruoyi.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @program: kai-yu-cloud
 * @description: 设备管理
 * @author: xiaojuzi
 * @create: 2024-04-19 15:03
 **/
@RestController
@RequestMapping("/content/device")
@Api(value = "设备管理相关接口",tags = "设备管理相关接口")
public class DeviceController {


    @Autowired
    private IDeviceService deviceService;


    @ApiOperation("根据场景id获取画小宇设备列表")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getDeviceListBySceneid")
    public RestResponse<Object> getDeviceListBySceneid(@RequestParam("sceneid") String sceneid) {
        List<Device> devices = deviceService.getDeviceListBySceneid(sceneid);
        if (devices == null || devices.size() == 0) {
            return RestResponse.validfail("未找到设备id");
        }
        return RestResponse.success(devices);
    }


    @ApiOperation("根据场景id获取外设设备列表")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getExternalDeviceListBySceneid")
    public RestResponse<Object> getExternalDeviceListBySceneid(@RequestParam("sceneid") String sceneid) {
        List<ExternalDevice> devices =deviceService.getExternalDeviceListBySceneid(sceneid);
        if (devices == null || devices.size() == 0) {
            return RestResponse.validfail("未找到设备id");
        }
        return RestResponse.success(devices);
    }


    @ApiOperation("前端播放视频选择场景返回-获取用户场景列表")
    @RequiresRoles(value = {"admin","common"}, logical = Logical.OR)
    @PostMapping("/getSceneList")
    public RestResponse<Object> getSceneList(@RequestParam("phone") String phone) {
        if (StringUtils.isNotEmpty(phone))
        {
            List sceneList = deviceService.getSceneList(phone);

            if (sceneList == null || sceneList.size() == 0) {
                return RestResponse.success("还没创建场景列表");
            }
            return RestResponse.success(sceneList);
        }
        return RestResponse.validfail("手机号不能为空");
    }


    @ApiOperation("前端播放视频选择场景返回-用户场景下所有设备列表")
    @RequiresRoles(value = {"admin","common"}, logical = Logical.OR)
    @PostMapping("/getAllDeviceListBySceneid")
    public RestResponse<Object> getAllDeviceListBySceneid(@RequestParam("sceneid") String sceneid) {
        if (StringUtils.isNotEmpty(sceneid)) {
            Map devices = deviceService.getAllDeviceListBySceneid(sceneid);

            if (devices == null || devices.size() == 0) {
                return RestResponse.validfail("未找到设备列表");
            }
            return RestResponse.success(devices);
        }
        return RestResponse.validfail("场景id不能为空");
    }




}
