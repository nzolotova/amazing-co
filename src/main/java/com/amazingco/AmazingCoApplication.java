package com.amazingco;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EntityScan
@EnableSpringConfigured
@SpringBootApplication
@EnableSpringDataWebSupport
public class AmazingCoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AmazingCoApplication.class).build().run(args);
    }

}
