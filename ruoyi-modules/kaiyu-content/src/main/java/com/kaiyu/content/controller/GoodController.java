package com.kaiyu.content.controller;

import com.kaiyu.content.domain.Category;
import com.kaiyu.content.domain.Good;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.dto.QueryAdminCourseDto;
import com.kaiyu.content.domain.dto.QueryAdminGoodDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import com.kaiyu.content.service.IGoodService;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    public PageResult<Good> getCourseByMultipleConditions(@RequestParam(value = "pageNo", defaultValue = "1") Long pageNo,
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

}
