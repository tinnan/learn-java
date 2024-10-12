package com.example.mainproject.controller;

import com.example.libproject.utils.CommonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainProjectController {

    @GetMapping("/")
    public String get() {
        return CommonUtils.sayHelloTo("James", "Moa");
    }
}
