package com.example.clean_code;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableAsync
@EnableScheduling
public class CleanCodeApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(CleanCodeApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
