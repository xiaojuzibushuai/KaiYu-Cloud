package com.kaiyu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaiyu.content.domain.*;
import com.kaiyu.content.domain.dto.EditGoodDto;
import com.kaiyu.content.domain.dto.QueryAdminGoodDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import com.kaiyu.content.mapper.GoodMapper;
import com.kaiyu.content.service.IGoodService;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public RestResponse deleteGoodsById(Long goodId) {
        int i = goodMapper.deleteById(goodId);
        if (i <= 0){
            KaiYuEducationException.cast("商品删除失败");
//            return RestResponse.validfail("删除商品失败");
        }
        return RestResponse.success("删除商品成功");
    }

    @Override
    @Transactional
    public RestResponse saveGoods(EditGoodDto goodDto) {
        if (goodDto.getId() != null && goodDto.getId() > 0 ){
            //商品已经存在 修改
            Good good = new Good();
            BeanUtils.copyProperties(goodDto, good);

            good.setUpdateTime(LocalDateTime.now());
            int i = goodMapper.updateById(good);
            if (i <= 0){
                KaiYuEducationException.cast("商品修改失败");
            }
            return RestResponse.success("商品修改成功");
        }else {
            //新增商品
            Good good = new Good();
            BeanUtils.copyProperties(goodDto, good);

            //查询有没这个类型的商品
            Good good1 = goodMapper.selectOne(new LambdaQueryWrapper<Good>().eq(Good::getGoodName, goodDto.getGoodName())
                    .and(w -> w.eq(Good::getGoodsType, goodDto.getGoodsType())));
            if (good1 != null){
                return RestResponse.validfail("该分类下的商品已存在");
            }
            good.setCreateTime(LocalDateTime.now());
            int i = goodMapper.insert(good);
            if (i <= 0){
                KaiYuEducationException.cast("商品新增失败");
            }
            return RestResponse.success("商品新增成功");
        }

    }

}
