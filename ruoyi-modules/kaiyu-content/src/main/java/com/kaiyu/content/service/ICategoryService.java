package com.kaiyu.content.service;

import com.kaiyu.content.domain.Category;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.RestResponse;
import com.kaiyu.content.domain.dto.EditCategoryDto;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 16:36
 **/
public interface ICategoryService {
    /**
     *查询课程分类列表 xiaojuzi
     */
    List<Category> getCategory();

    PageResult<Category> getBackCategory(PageParams pageParams);

    RestResponse saveBackCategory(EditCategoryDto categoryDto);

    RestResponse deleteBackCategory(Long categoryId);


}
