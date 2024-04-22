package com.kaiyu.media.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @description 上传普通文件请求参数
 *  @author xiaojuzi
 */
 @Data
 @ToString
public class UploadFileParamsDto {

 /**
  * 文件名称
  */
 @ApiModelProperty("文件名称")
 private String filename;

 /**
  * 文件content-type
 */
 @ApiModelProperty("文件content-type")
 private String contentType;

 /**
  * 文件类型（文档，图片，视频）
  * 001001 图片 001002 视频 001003 音频 001004 文档 001005 其他
  */
 @ApiModelProperty("文件类型")
 private String fileType;
 /**
  * 文件大小
  */
 @ApiModelProperty("文件大小")
 private Long fileSize;

 /**
  * 标签
  */
 @ApiModelProperty("标签")
 private String tags;

 /**
  * 上传人
  */
 @ApiModelProperty("上传人")
 private String username;

 /**
  * 备注
  */
 @ApiModelProperty("备注")
 private String remark;
}
