package com.kaiyu.content;

import com.kaiyu.content.domain.Course;
import com.kaiyu.content.mapper.CourseMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 14:55
 **/
@SpringBootTest
@MapperScan({"com.kaiyu.content.mapper"})
public class KaiyuContentApplicationTest {


    @Autowired(required = false)
    private CourseMapper courseMapper;

    @Test
    public void getCourse() {

        List<Course> result =null;
        Long courseId =273L;
        Long categoryId = 7L;
        String courseClass = null;
        result = courseMapper.getCourse(courseId,categoryId,courseClass);
        System.out.println(result);

    }


}
