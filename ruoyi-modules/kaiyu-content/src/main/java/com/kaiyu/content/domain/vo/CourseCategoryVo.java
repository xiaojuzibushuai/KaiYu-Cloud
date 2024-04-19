package com.kaiyu.content.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-11 11:55
 **/
@Data
@ToString
public class CourseCategoryVo {

    private Long id;

    private String title;

    private String category_title;


    private String detail;

    private String savePath;

    private Long categoryId;

    private String voiceFiles;

    private String dataFiles;

    private String lrcFiles;

    private String imgFiles;

    private int isPublic;

    private int loveNumber;

    private int hardNumber;

    private int indexShow;

    private Date uptime;

    private int priority;

    private int playTime;

    private String courseClass;

    private String volume;



}
