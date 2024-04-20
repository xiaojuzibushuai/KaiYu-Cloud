package com.kaiyu.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaiyu.content.domain.*;
import com.kaiyu.content.domain.dto.EditCategoryDto;
import com.kaiyu.content.mapper.CategoryMapper;
import com.kaiyu.content.mapper.CourseMapper;
import com.kaiyu.content.mapper.DeviceCategoryMapper;
import com.kaiyu.content.mapper.DeviceMapper;
import com.kaiyu.content.service.ICategoryService;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 16:37
 **/
@Service
public class CategoryServiceImpl implements ICategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    DeviceCategoryMapper deviceCategoryMapper;

    @Autowired
    DeviceMapper deviceMapper;

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
    public PageResult<Category> getBackCategory(PageParams pageParams) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.groupBy(Category::getId);
//        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        Page<Category> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());

        Page<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);
        List<Category> records = categoryPage.getRecords();
        long total = categoryPage.getTotal();

        PageResult<Category> categoryPageResult = new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());

        return categoryPageResult;
    }

    @Override
    @Transactional
    public RestResponse saveBackCategory(EditCategoryDto categoryDto) {
        if ((categoryDto.getId() != null) && (categoryDto.getId() > 0)){
            //该分类已存在 修改
            LambdaUpdateWrapper<Category> wrapper = new LambdaUpdateWrapper<Category>()
                    .eq(Category::getId, categoryDto.getId())
                    .set(StringUtils.isNotEmpty(categoryDto.getTitle()), Category::getTitle, categoryDto.getTitle())
                    .set(StringUtils.isNotEmpty(categoryDto.getDetail()), Category::getDetail, categoryDto.getDetail())
                    .set(StringUtils.isNotEmpty(categoryDto.getSavePath()), Category::getSavePath, categoryDto.getSavePath())
                    .set(categoryDto.getPriority() > 0, Category::getPriority, categoryDto.getPriority())
                    .set(categoryDto.getIndexCate() >= 0, Category::getIndexCate, categoryDto.getIndexCate());

            int update = categoryMapper.update(null, wrapper);
            if (update > 0){
                //删除缓存
                Boolean getAllCategories = redisTemplate.delete("getAllCategories");
                log.info("删除缓存：{}", getAllCategories);
                return RestResponse.success("修改成功");
            }else {
                return RestResponse.validfail("修改失败");
            }

        }else {
            //新增
            //分类不存在 新增
            Category category = new Category();
            BeanUtils.copyProperties(categoryDto, category);
            int insert = categoryMapper.insert(category);
            if (insert > 0){
                //新增设备分类关系
                List<Device> devices = deviceMapper.selectList(new LambdaQueryWrapper<Device>().eq(Device::getIs_auth, 1));
                List<DeviceCategory> deviceCategoryList = new ArrayList<>();
                devices.forEach(device -> {
                    DeviceCategory deviceCategory = new DeviceCategory();
                    deviceCategory.setCategory_id(category.getId());
                    deviceCategory.setDevice_id(device.getId());
//                    deviceCategoryMapper.insert(deviceCategory);
                    deviceCategoryList.add(deviceCategory);
                });
                int i = deviceCategoryMapper.insertBatch(deviceCategoryList);
                if (i > 0){

                    //删除缓存
                    Boolean getAllCategories = redisTemplate.delete("getAllCategories");
                    log.info("删除缓存：{}", getAllCategories);

                    return RestResponse.success("新增成功");
                }else {
                    KaiYuEducationException.cast("新增设备分类关系失败");
                    return RestResponse.validfail("新增设备分类关系失败");
                }

            }else {
                KaiYuEducationException.cast("新增分类失败");
                return RestResponse.validfail("新增分类失败");
            }
        }
    }

    @Override
    @Transactional
    public RestResponse deleteBackCategory(Long categoryId) {

        //删除设备分类表记录

        LambdaQueryWrapper<DeviceCategory> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(DeviceCategory::getCategory_id, categoryId);
        queryWrapper1.select(DeviceCategory::getId);
        List<DeviceCategory> deviceCategories = deviceCategoryMapper.selectList(queryWrapper1);
        if (deviceCategories.size() != 0){

            // 获取所有符合条件的记录的主键列表
            List<Long> ids = deviceCategories.stream().map(DeviceCategory::getId).collect(Collectors.toList());

            // 批量删除
            deviceCategoryMapper.deleteBatchIds(ids);
        }


        //将对应分类下的课程滞空

        LambdaUpdateWrapper<Course> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Course::getCategoryId, categoryId);
        updateWrapper.set(Course::getCategoryId, null);

        // 执行批量更新
        courseMapper.update(null,updateWrapper);

        //删除分类
        int delete = categoryMapper.deleteById(categoryId);

        if (delete > 0){
            //删除缓存
            Boolean getAllCategories = redisTemplate.delete("getAllCategories");
            log.info("删除缓存：{}", getAllCategories);
            return RestResponse.success("删除分类成功");
        }else {
            log.info("删除分类失败，约束条件不满足");
            KaiYuEducationException.cast("删除分类失败");

            return RestResponse.validfail("删除分类失败");
        }
    }

}
