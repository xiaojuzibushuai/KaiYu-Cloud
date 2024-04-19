package com.kaiyu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.content.domain.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 16:39
 **/
public interface CategoryMapper extends BaseMapper<Category> {


//    @select("select * from category")
    List<Category> getCategory();

    @Select("select id from category where title like CONCAT('%', #{categoryName}, '%') ")
    List<Long> getCategoryIds(@Param("categoryName") String categoryName);

}
