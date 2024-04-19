package com.kaiyu.content.controller;

import com.kaiyu.content.domain.Category;
import com.kaiyu.content.domain.Course;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.service.ICategoryService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
