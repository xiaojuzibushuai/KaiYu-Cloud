package com.kaiyu.content.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-02 10:20
 **/
@Data
@ToString
public class QueryCourseDto {


    @ApiModelProperty("课程id")
    private Long courseId;
    @ApiModelProperty("课程分类id")
    private Long categoryId;
    @ApiModelProperty("课程班级")
    private String courseClass;



}
