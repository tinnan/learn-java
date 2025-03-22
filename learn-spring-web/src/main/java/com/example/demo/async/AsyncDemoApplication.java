package com.example.demo.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"com.example.demo.async.clients"})
@SpringBootApplication(scanBasePackages = "com.example.demo.async")
public class AsyncDemoApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(AsyncDemoApplication.class);
		springApplication.setAdditionalProfiles("async");
		springApplication.run(args);
	}

}
