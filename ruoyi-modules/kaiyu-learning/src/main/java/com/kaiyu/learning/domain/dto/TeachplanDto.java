package com.kaiyu.learning.domain.dto;

import com.kaiyu.learning.domain.TeachplanMedia;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 09:41
 **/
@Data
@ToString
public class TeachplanDto {

    @ApiModelProperty(value = "课程媒资信息", required = true)
    private List<TeachplanMedia> teachplanMedia;

}
