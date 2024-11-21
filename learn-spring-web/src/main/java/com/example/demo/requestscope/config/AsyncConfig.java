package com.example.demo.requestscope.config;

import com.example.demo.requestscope.config.context.ContextAwarePoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean
    public TaskExecutor asyncTaskExecutor() {
        ContextAwarePoolExecutor executor = new ContextAwarePoolExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        return executor;
    }
}
