package com.guru.future;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan
@EnableConfigurationProperties
@EnableAsync
@SpringBootApplication
@EnableCaching
public class FutureApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(FutureApplication.class);
        builder.headless(false).run(args);
//        SpringApplication.run(FutureApplication.class, args);
    }

}
