package com.kaiyu.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.content.domain.Category;
import com.kaiyu.content.mapper.CategoryMapper;
import com.kaiyu.content.service.ICategoryService;
import com.ruoyi.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 16:37
 **/
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public List<Category> getCategory() {
        
        //先查缓存
        String categoriesKey = "getAllCategories";
        String categoriesJson = (String) redisTemplate.opsForValue().get(categoriesKey);

        if (StringUtils.isNotEmpty(categoriesJson)){
            List<Category> categoryList = JSON.parseArray(categoriesJson, Category.class);
            return categoryList;
        }else {
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Category::getIndexCate, 1).groupBy(Category::getId);
            List<Category> categoryList = categoryMapper.selectList(queryWrapper);

            //没有则缓存
            redisTemplate.opsForValue().set(categoriesKey, JSON.toJSONString(categoryList),30 + new Random().nextInt(100), TimeUnit.MINUTES);
            return categoryList;
        }

    }

    @Override
    public List<Category> getBackCategory() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.groupBy(Category::getId);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        return categoryList;
    }


}
