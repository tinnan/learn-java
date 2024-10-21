package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.model.GetParams;
import com.example.demo.controllerparam.model.PostParams;
import feign.Param;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface DemoServerApi {

    @GetMapping("/server")
    GetParams serverGet(@RequestHeader HttpHeaders headers,
        @ParameterObject @Valid GetParams params);

    @PostMapping("/server")
    PostParams serverPost(@RequestHeader HttpHeaders headers, @RequestBody @Valid PostParams request);
}
