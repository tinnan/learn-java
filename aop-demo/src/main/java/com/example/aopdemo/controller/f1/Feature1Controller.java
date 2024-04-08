package com.example.aopdemo.controller.f1;

import com.example.aopdemo.aop.annotation.EnableElapsedTimeLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feature1")
@EnableElapsedTimeLog
public class Feature1Controller {

    @GetMapping
    public String greet() {
        return "Hi there!";
    }
}
