package com.example.demo.controllerparam.controller;

import com.example.demo.controllerparam.client.ServerFeign;
import com.example.demo.controllerparam.model.GetParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoClientController implements DemoClientApi {

    private final ServerFeign serverFeign;

    @GetMapping("/client")
    public GetParams clientGet(String name, Integer age) {
        return serverFeign.serverGet(new HttpHeaders(), GetParams.builder().name(name).age(age).build());
    }
}
