package com.kaiyu.content.controller;

import com.kaiyu.content.domain.RestResponse;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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



    @ApiOperation("根据场景id获取设备列表")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getDeviceListBySceneid")
    public RestResponse<Object> getDeviceListBySceneid(@RequestParam("sceneid") String sceneid) {

        return RestResponse.success();
    }





}
