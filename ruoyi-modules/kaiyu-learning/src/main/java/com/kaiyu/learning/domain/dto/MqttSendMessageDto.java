package com.kaiyu.learning.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-18 17:32
 **/
@Data
@ToString
public class MqttSendMessageDto {

    @ApiModelProperty(value = "消息质量")
    @NotNull(message = "qos 不能为空")
    int qos;

    @ApiModelProperty(value = "是否保留消息")
    @NotBlank(message = "retained 不能为空")
    Boolean retained;

    @ApiModelProperty(value = "消息主题")
    @NotBlank(message = "topic 不能为空")
    String topic;

    @ApiModelProperty(value = "消息内容")
    @NotBlank(message = "message 不能为空")
    String message;


}
