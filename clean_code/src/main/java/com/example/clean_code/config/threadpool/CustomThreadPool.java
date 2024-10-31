package com.example.clean_code.config.threadpool;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class CustomThreadPool {

    private final ThreadPoolProperties threadPoolProperties;

    @Bean(name = "asyncExecutor")
    public ThreadPoolExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(this.threadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveTime());
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }
}
