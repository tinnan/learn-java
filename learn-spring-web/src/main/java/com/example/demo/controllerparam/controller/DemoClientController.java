package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.client.ServerFeign;
import com.example.demo.controllerparam.model.GetParams;
import com.example.demo.controllerparam.model.PostParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoClientController implements DemoClientApi {

    private final ServerFeign serverFeign;

    @Override
    public Object clientGet(String mode, String name, Integer age) {
        HttpHeaders headers = new HttpHeaders();
        if ("q".equals(mode)) {
            return serverFeign.serverGet(headers, GetParams.builder().name(name).age(age).build());
        } else {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return serverFeign.serverPost(headers, PostParams.builder().name(name).age(age).build());
        }
    }
}
