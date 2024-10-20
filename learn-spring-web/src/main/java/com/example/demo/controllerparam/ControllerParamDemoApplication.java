package com.example.demo.controllerparam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@EnableFeignClients
@SpringBootApplication
public class ControllerParamDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ControllerParamDemoApplication.class)
            .profiles("controllerparam")
            .run(args);
    }
}
