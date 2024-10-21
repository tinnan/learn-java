package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.model.GetParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface DemoClientApi {

    @GetMapping("/client")
    GetParams clientGet(@RequestParam String name,
        @RequestParam(required = false) Integer age);
}
