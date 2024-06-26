package com.kaiyu.content.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 18:34
 **/
@Data
@ToString
public class QueryAdminGoodDto {

    @ApiModelProperty("商品名字")
    private String  goodName;
    @ApiModelProperty("商品关键字")
    private String  keyword;
    @ApiModelProperty("是否上架 1是 0不是")
    private Integer isShow;
    @ApiModelProperty("商品类型")
    private String goodsType;
}
