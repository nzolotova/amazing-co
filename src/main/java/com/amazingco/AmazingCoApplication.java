package com.amazingco;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

public class AmazingCoApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AmazingCoApplication.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
}
