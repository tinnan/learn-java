package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.model.GetParams;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoServerController implements DemoServiceApi {

    @GetMapping("/server")
    public GetParams serverGet(HttpHeaders headers, GetParams params) {
        return params;
    }
}
