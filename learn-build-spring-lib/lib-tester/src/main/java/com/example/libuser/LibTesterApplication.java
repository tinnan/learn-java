package com.example.libuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.libuser", "org.example.security"})
public class LibTesterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibTesterApplication.class, args);
    }
}
