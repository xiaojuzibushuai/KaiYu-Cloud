package com.kaiyu.content.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "BindTeachplanMediaDto", description = "教学计划-媒资绑定")
public class BindTeachplanMediaDto {
    @ApiModelProperty(value = "媒资文件id", required = true)
    @NotBlank(message = "媒资文件ID不能为空")
    private String mediaId;
    @ApiModelProperty(value = "媒资文件名称", required = true)
    @NotBlank(message = "媒资文件名称不能为空")
    private String fileName;
//    @ApiModelProperty(value = "课程计划标识", required = true)
//    private Long teachplanId;
    @ApiModelProperty(value = "课程标识", required = true)
    @NotNull(message = "课程标识不能为空")
    private Long courseId;
}
