package com.kaiyu.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaiyu.order.domain.MqMessage;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-28 10:50
 **/
public interface MqMessageService extends IService<MqMessage> {

    /**
     * 添加消息
     * @param businessKey1 业务id
     * @param businessKey2 业务id
     * @param businessKey3 业务id
     * @return  消息内容
     */
    MqMessage addMessage(String messageType, String businessKey1, String businessKey2, String businessKey3);

    /**
     * 完成任务
     * @param id 消息id
     * @return int 更新成功：1
     */
    int completed(long id);

    /**
     * 完成阶段任务
     * @param id 消息id
     * @return int 更新成功：1
     */
    int completedStageOne(long id);

    int completedStageTwo(long id);

    int completedStageThree(long id);

    int completedStageFour(long id);

    /**
     * 查询阶段状态
     * @param id
     * @return int
     */
    int getStageOne(long id);

    int getStageTwo(long id);

    int getStageThree(long id);

    int getStageFour(long id);
}
