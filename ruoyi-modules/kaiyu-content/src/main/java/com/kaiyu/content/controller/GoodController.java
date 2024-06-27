package com.kaiyu.content.controller;

import com.kaiyu.content.domain.*;
import com.kaiyu.content.domain.dto.EditCategoryDto;
import com.kaiyu.content.domain.dto.EditGoodDto;
import com.kaiyu.content.domain.dto.QueryAdminCourseDto;
import com.kaiyu.content.domain.dto.QueryAdminGoodDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import com.kaiyu.content.service.IGoodService;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 18:18
 **/
@RestController
@RequestMapping("/content/goods")
@Api(value = "商品基本信息接口",tags = "商品基本信息接口")
public class GoodController {

    @Autowired
    IGoodService goodService;

    @PostMapping("/back/getGoodsByConditions")
    @RequiresRoles(value = {"admin","common"}, logical = Logical.OR)
    @ApiOperation("查询商品列表多条件筛选")
    public PageResult<Good> getGoodsByConditions(@RequestParam(value = "pageNo", defaultValue = "1") Long pageNo,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
                                                          @RequestBody QueryAdminGoodDto queryGoodDto){
        PageParams pageParams = new PageParams();
        if (pageNo >= 1) {
            pageParams.setPageNo(pageNo);
        }
        if (pageSize >= 1) {
            pageParams.setPageSize(pageSize);
        }

        return goodService.queryGoodByMultipleConditions(pageParams, queryGoodDto);

    }


    @PostMapping("/back/saveGoods")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("后台管理-修改或新增商品信息")
    public RestResponse saveGoods(@RequestBody EditGoodDto goodDto){
        if (StringUtils.isBlank(goodDto.getGoodName()) || StringUtils.isBlank(goodDto.getKeyword())
                || goodDto.getPrice() < 0 || StringUtils.isBlank(goodDto.getGoodsType())
                || StringUtils.isBlank(goodDto.getOutBusinessId())){
            return RestResponse.validfail("参数不合法");
        }

        return goodService.saveGoods(goodDto);
    }


    @DeleteMapping("/back/deleteGoods")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("后台管理-删除商品")
    public RestResponse deleteGoods(@RequestParam(value = "goodId", required = true) Long goodId){
        if (goodId < 0) {
            return RestResponse.validfail("商品id不合法");
        }
        return goodService.deleteGoodsById(goodId);
    }




}
