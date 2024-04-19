package com.kaiyu.content.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 11:06
 **/
@Data
@ToString
public class QueryAdminCourseDto {
    @ApiModelProperty("课程标题")
    private String  courseTitle;
    @ApiModelProperty("课程分类名字")
    private String  courseCategory;
}
