package com.kaiyu.order.domain.dto;

import com.kaiyu.order.domain.PayRecord;
import lombok.Data;
import lombok.ToString;

/**
 * @program: KaiYu-Cloud
 * @description: 支付记录dto
 * @author: xiaojuzi
 * @create: 2024-06-17 17:03
 **/
@Data
@ToString
public class PayRecordDto extends PayRecord {

    //二维码
    private String qrcode;

}
