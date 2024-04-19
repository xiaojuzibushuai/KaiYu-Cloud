package com.kaiyu.learning.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @program: kai-yu-cloud
 * @description: XxlJob开发示例（Bean模式）
 *   开发步骤：
 *   1、任务开发：在Spring Bean实例中，开发Job方法；
 *   2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法",
 *   destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *   3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *   4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，
 *   可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 * @author: xiaojuzi
 * @create: 2024-04-03 11:54
 *
 **/
@Component
public class SampleXxlJob {

    private static final Logger log = LoggerFactory.getLogger(SampleXxlJob.class);

    @XxlJob("testJob")
    public void testJob() {
        System.out.println("开始执行testJob.......");
        log.debug("开始执行.......");
    }

    @XxlJob("shardingJobHandler")
    public void shardingJob() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        log.debug("shardIndex:{}, shardTotal:{}", shardIndex, shardTotal);
    }
}
