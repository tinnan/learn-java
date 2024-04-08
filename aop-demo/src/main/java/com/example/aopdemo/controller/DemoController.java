package com.example.aopdemo.controller;

import com.example.aopdemo.model.DemoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
@Slf4j
public class DemoController {

    @GetMapping
    public DemoResponse getInfo(@RequestParam("name") String name) {
        return new DemoResponse("Hello, " + name + "!");
    }
}
