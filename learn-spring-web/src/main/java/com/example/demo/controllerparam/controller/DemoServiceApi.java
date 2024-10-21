package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.model.GetParams;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface DemoServiceApi {

    @GetMapping("/server")
    GetParams serverGet(@RequestHeader HttpHeaders headers,
        @ParameterObject @Valid GetParams params);
}
