package org.example.libtester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example.libtester", "org.example.security"})
public class LibTesterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibTesterApplication.class, args);
    }
}
