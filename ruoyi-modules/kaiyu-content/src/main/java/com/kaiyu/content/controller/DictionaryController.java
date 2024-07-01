package com.kaiyu.content.controller;

import com.kaiyu.content.service.DictionaryService;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kaiyu.content.domain.Dictionary;

import java.util.List;

/**
 * @program: KaiYu-Cloud
 * @description: 数据字典
 * @author: xiaojuzi
 * @create: 2024-07-01 11:12
 **/
@RestController
@RequestMapping("/content/dictionary")
@Api(value = "数据字典基本信息接口",tags = "数据字典基本信息接口")
public class DictionaryController {


    @Autowired
    private DictionaryService  dictionaryService;

    @GetMapping("/queryAll")
//    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @ApiOperation("查询数据字典列表")
    public List<Dictionary> queryAll() {
        return dictionaryService.queryAll();
    }

    @GetMapping("/getByCode/{code}")
//    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @ApiOperation("根据字典类型查询数据")
    public Dictionary getByCode(@PathVariable String code) {
        return dictionaryService.getByCode(code);
    }



}
