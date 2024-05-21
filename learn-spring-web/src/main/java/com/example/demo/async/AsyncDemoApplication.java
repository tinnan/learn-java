package com.example.demo.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.example.demo.async")
@EnableAsync
@EnableFeignClients(basePackages = {"com.example.demo.async.clients"})
public class AsyncDemoApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(AsyncDemoApplication.class);
		springApplication.setAdditionalProfiles("async");
		springApplication.run(args);
	}

}
