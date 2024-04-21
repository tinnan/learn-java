package com.example.demorelations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.example.demorelations", exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
public class DemoRelationsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoRelationsApplication.class, args);
    }
}
