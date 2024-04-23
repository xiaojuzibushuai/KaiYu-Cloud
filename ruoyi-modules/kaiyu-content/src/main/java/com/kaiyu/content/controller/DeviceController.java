package com.kaiyu.content.controller;

import com.kaiyu.content.domain.Device;
import com.kaiyu.content.domain.DeviceGroup;
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


    @ApiOperation("根据场景id获取设备列表")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getDeviceListBySceneid")
    public RestResponse<Object> getDeviceListBySceneid(@RequestParam("sceneid") String sceneid) {
        List<Device> devices = deviceService.getDeviceListBySceneid(sceneid);
        if (devices == null || devices.size() == 0) {
            return RestResponse.validfail("未找到设备id");
        }
        return RestResponse.success(devices);
    }


    @ApiOperation("获取用户场景列表")
    @RequiresRoles(value = {"common"}, logical = Logical.OR)
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
        return RestResponse.validfail("只限用户端账号获取");
    }

}
