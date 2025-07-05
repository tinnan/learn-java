package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.model.DemoRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping
    DemoRequest get(DemoRequest request) {
        return request;
    }
}
