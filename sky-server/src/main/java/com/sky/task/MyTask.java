package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务类,自定义定时任务类
 */
@Slf4j
@Component
public class MyTask {

    /**
     * 定时任务
     * 每隔五秒触发一次这个函数
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void task1(){
        log.info("定时任务开始执行...");
    }
}
