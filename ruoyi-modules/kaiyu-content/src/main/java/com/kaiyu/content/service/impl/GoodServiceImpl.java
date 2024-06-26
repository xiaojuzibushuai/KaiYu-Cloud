package com.kaiyu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaiyu.content.domain.Category;
import com.kaiyu.content.domain.Good;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.dto.QueryAdminGoodDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import com.kaiyu.content.mapper.GoodMapper;
import com.kaiyu.content.service.IGoodService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 18:28
 **/
@Service
public class GoodServiceImpl implements IGoodService {

    private static final Logger log = LoggerFactory.getLogger(GoodServiceImpl.class);

    @Autowired
    GoodMapper goodMapper;

    @Override
    public PageResult<Good> queryGoodByMultipleConditions(PageParams pageParams, QueryAdminGoodDto queryGoodDto) {

        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryGoodDto.getGoodName())) {
            queryWrapper.likeRight(Good::getGoodName, queryGoodDto.getGoodName());
        }

        if (StringUtils.isNotBlank(queryGoodDto.getKeyword())) {
            queryWrapper.like(Good::getKeyword, queryGoodDto.getKeyword());
        }

        if (queryGoodDto.getIsShow() !=null && (queryGoodDto.getIsShow() == 1 || queryGoodDto.getIsShow() == 0 )) {
            queryWrapper.eq(Good::getIsShow, queryGoodDto.getIsShow());
        }

        if (StringUtils.isNotBlank(queryGoodDto.getGoodsType())) {
            queryWrapper.eq(Good::getGoodsType, queryGoodDto.getGoodsType());
        }
//        queryWrapper.groupBy(Category::getId);
//        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        Page<Good> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());

        Page<Good> goodPage = goodMapper.selectPage(page, queryWrapper);
        List<Good> records = goodPage.getRecords();
        long total = goodPage.getTotal();

        PageResult<Good> goodPageResult = new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());

        return goodPageResult;
    }
}