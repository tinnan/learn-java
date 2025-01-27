package com.example.demo.largeresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LargeResponseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LargeResponseApplication.class, args);
    }
}
