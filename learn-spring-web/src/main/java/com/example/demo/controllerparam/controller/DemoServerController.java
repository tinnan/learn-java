package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.model.GetParams;
import com.example.demo.controllerparam.model.PostParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoServerController implements DemoServerApi {

    @Override
    public GetParams serverGet(HttpHeaders headers, GetParams params) {
        log.info("{}", params);
        return params;
    }

    @Override
    public PostParams serverPost(HttpHeaders headers, PostParams request) {
        log.info("{}", request);
        return request;
    }
}
