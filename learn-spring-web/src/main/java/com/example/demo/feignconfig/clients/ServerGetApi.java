package com.example.demo.feignconfig.clients;

import org.springframework.web.bind.annotation.GetMapping;

public interface ServerGetApi {

    @GetMapping("/server")
    String get();
}
