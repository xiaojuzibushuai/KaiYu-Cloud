package com.kaiyu.learning.feignclient;

import com.kaiyu.learning.domain.RestResponse;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-29 08:47
 **/
@FeignClient(contextId = "RemoteOrderService" , value = ServiceNameConstants.ORDER_SERVICE,fallbackFactory = RemoteOrderFallbackFactory.class)
public interface RemoteOrderService {

    @ApiOperation("查询用户订单商品列表")
    //    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @PostMapping("/order/getUserOrderGoodsList")
    public RestResponse getUserOrderGoodsList(@RequestParam("orderId")String orderId);



}
