package com.kaiyu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.learning.domain.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-02 15:25
 **/
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {
    /**
     * 根据分片参数获取待处理任务
     * xiaojuzi
     * @param shardTotal    分片总数
     * @param shardIndex    分片序号
     * @param count         任务数
     * @return
     */
    @Select("SELECT * FROM media_process t WHERE t.id % #{shardTotal} = #{shardIndex} and (t.status = '1' or t.status = '3') and " +
            " t.fail_count < 3 LIMIT #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);

    /**
     * 开始任务是否成功
     * xiaojuzi
     * @param id
     * @return
     */
    @Update("update media_process m set m.status='4' where ( m.status='1' or m.status='3') and m.fail_count<3 and m.id=#{id}")
    int startTask(@Param("id") long id);

}
