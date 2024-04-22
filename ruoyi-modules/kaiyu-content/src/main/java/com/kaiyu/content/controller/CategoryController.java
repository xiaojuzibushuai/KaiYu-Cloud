package com.kaiyu.content.controller;

import com.kaiyu.content.domain.*;
import com.kaiyu.content.domain.dto.EditCategoryDto;
import com.kaiyu.content.service.ICategoryService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.*;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 16:47
 **/
@RestController
@RequestMapping("/content/category")
@Api(value = "课程分类基本信息接口",tags = "课程分类基本信息接口")
public class CategoryController{

    @Autowired
    ICategoryService categoryService;

    /**
     * 查询课程分类列表
     * @return 课程分类集合
     * @author xiaojuzi
     */
    @GetMapping("/getCategory")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @ApiOperation("查询课程分类列表")
    public R<List<Category>> getCategory(){

        return R.ok(categoryService.getCategory());

    }


    @GetMapping("/back/getBackCategory")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("后台管理-查询课程分类列表")
    public PageResult<Category> getBackCategory(@RequestParam(value = "pageNo", defaultValue = "1") Long pageNo,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize){

        PageParams pageParams = new PageParams();

        if (pageNo >= 1) {
            pageParams.setPageNo(pageNo);
        }
        if (pageSize >= 1) {
            pageParams.setPageSize(pageSize);
        }

        return categoryService.getBackCategory(pageParams);

    }


    @PostMapping("/back/saveBackCategory")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("后台管理-新增或保存课程分类列表")
    public RestResponse saveBackCategory(@RequestBody EditCategoryDto categoryDto){
        if (StringUtils.isEmpty(categoryDto.getTitle()) || StringUtils.isBlank(categoryDto.getTitle())){
            return RestResponse.validfail("课程分类标题不能为空");
        }

        return categoryService.saveBackCategory(categoryDto);
    }


    @DeleteMapping("/back/deleteBackCategory")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("后台管理-删除课程分类列表")
    public RestResponse deleteBackCategory(@RequestParam(value = "categoryId", required = true) Long categoryId){
        if (categoryId < 0) {
            return RestResponse.validfail("课程分类id不合法");
        }

        return categoryService.deleteBackCategory(categoryId);
    }


}
