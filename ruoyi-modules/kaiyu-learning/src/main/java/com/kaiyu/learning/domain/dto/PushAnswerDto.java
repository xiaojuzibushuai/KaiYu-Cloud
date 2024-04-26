package com.kaiyu.learning.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-24 14:14
 **/
@Data
@ToString
public class PushAnswerDto {

    private String gametype;

    private String answer;

    private String parentid;

    private String courseid;

    private String sceneid;


}
