package com.guru.future;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan
@EnableConfigurationProperties
@EnableAsync
@SpringBootApplication
public class FutureApplication {

    public static void main(String[] args) {
        SpringApplication.run(FutureApplication.class, args);
    }

}