package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

import javax.swing.*;

@SpringBootApplication
@Slf4j
public class DemoApplication {

    public static void main(String[] args) {
        String[] activeProfiles = args[0].split("=")[1].split(",");
        boolean needWebEnvironment = false;
        for (String profile : activeProfiles) {
            if (profile.contains("api")) {
                needWebEnvironment = true;
                break;
            }
        }
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DemoApplication.class);
        if (!needWebEnvironment) {
            // To avoid starting web application server when it is not needed.
            builder.web(WebApplicationType.NONE);
        }
        builder.build().run(args);
//		System.exit(SpringApplication.exit(SpringApplication.run(DemoApplication.class, args)));
    }

}
