package com.example.demo.configprop;

import com.example.demo.configprop.config.ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
public class ConfigPropDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigPropDemoApplication.class)
            .web(WebApplicationType.NONE)
            .profiles("configprop")
            .run(args);
    }

    @Bean
    public CommandLineRunner run(ConfigProperties configProperties) {
        return args -> log.info("Config properties: {}", configProperties);
    }
}
