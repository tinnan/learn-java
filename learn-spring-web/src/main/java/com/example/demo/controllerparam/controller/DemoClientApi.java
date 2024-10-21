package com.example.demo.controllerparam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface DemoClientApi {

    @GetMapping("/client")
    Object clientGet(
        @RequestParam String mode,
        @RequestParam String name,
        @RequestParam(required = false) Integer age);
}
