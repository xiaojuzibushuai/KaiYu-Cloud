package com.kaiyu.content.service;

import com.kaiyu.content.domain.Good;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.RestResponse;
import com.kaiyu.content.domain.dto.EditGoodDto;
import com.kaiyu.content.domain.dto.QueryAdminGoodDto;

import java.util.List;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 18:27
 **/
public interface IGoodService {

    PageResult<Good> queryGoodByMultipleConditions(PageParams pageParams, QueryAdminGoodDto queryGoodDto);

    RestResponse deleteGoodsById(Long goodId);

    RestResponse saveGoods(EditGoodDto goodDto);

    Good getGoodsDetailById(Long goodId);

    List<Good> getGoodsListByIds(List<Long> goodIds);
}
