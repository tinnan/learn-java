package com.example.demo.feignconfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@RequiredArgsConstructor
public class FeignConfigDemoApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(FeignConfigDemoApplication.class);
        springApplication.setAdditionalProfiles("feignconfig");
        springApplication.run(args);
    }

    @Qualifier("server1")
    private final WireMockServer server1;
    @Qualifier("server2")
    private final WireMockServer server2;

    @PostConstruct
    public void setup() {
        server1.start();
        server2.start();
    }

    @PreDestroy
    public void cleanup() {
        server1.stop();
        server2.stop();
    }
}
