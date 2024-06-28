package com.kaiyu.learning.service.impl;

import com.kaiyu.learning.mapper.CourseTablesMapper;
import com.kaiyu.learning.service.CourseTablesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-28 14:14
 **/
@Service
public class CourseTablesServiceImpl implements CourseTablesService {

    private static final Logger log = LoggerFactory.getLogger(CourseTablesServiceImpl.class);

    @Autowired
    CourseTablesMapper courseTablesMapper;





}
