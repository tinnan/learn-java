package com.example.demo.multipart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.example.demo.multipart")
@EnableFeignClients(basePackages = "com.example.demo.multipart")
public class MultipartDemoApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MultipartDemoApplication.class);
        springApplication.setAdditionalProfiles("multipart");
        springApplication.run(args);
    }
}
