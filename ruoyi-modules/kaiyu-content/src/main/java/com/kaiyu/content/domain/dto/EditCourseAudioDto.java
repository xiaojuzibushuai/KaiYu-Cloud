package com.kaiyu.content.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-29 15:59
 **/
@Data
@ToString
public class EditCourseAudioDto {
    @ApiModelProperty("课程id")
    private Long courseId;
    @ApiModelProperty("课程集数")
    private Long episode;
    @ApiModelProperty("课程脚本")
    private List<HashMap<String,Object>> timePoint;


    @ApiModelProperty("课程脚本gameUrl")
    private String gameUrl;
    @ApiModelProperty("课程脚本timeId")
    private String timeId;
    @ApiModelProperty("课程脚本sendType")
    private String sendType;
    @ApiModelProperty("课程脚本startTime")
    private String startTime;
    @ApiModelProperty("课程脚本endTime")
    private String endTime;
    @ApiModelProperty("课程脚本formatStartTime")
    private String formatStartTime;
    @ApiModelProperty("课程脚本marks")
    private String marks;
    @ApiModelProperty("课程脚本data")
    private Map<String, Object> data;

}
