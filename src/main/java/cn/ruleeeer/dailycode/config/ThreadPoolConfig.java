package cn.ruleeeer.dailycode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author ruleeeer
 * @date 2021/10/5 14:36
 */
@Configuration
@Component
public class ThreadPoolConfig {

    @Bean("sendEmailThreadPool")
    public ThreadPoolTaskExecutor sendEmailThreadPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        taskExecutor.setThreadNamePrefix("sendEmailThreadPool--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        return taskExecutor;
    }

}