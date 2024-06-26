package com.kaiyu.content.service;

import com.kaiyu.content.domain.Good;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.dto.QueryAdminGoodDto;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 18:27
 **/
public interface IGoodService {

    PageResult<Good> queryGoodByMultipleConditions(PageParams pageParams, QueryAdminGoodDto queryGoodDto);
}
