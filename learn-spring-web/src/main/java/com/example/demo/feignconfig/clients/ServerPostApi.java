package com.example.demo.feignconfig.clients;

import org.springframework.web.bind.annotation.PostMapping;

public interface ServerPostApi {

    @PostMapping("/server")
    String post();
}
