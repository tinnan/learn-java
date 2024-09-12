package com.example.demo.feignconfig.clients;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "server-client", url = "${feign.server-client.url}",
    configuration = {RequestInterceptor.class})
public interface ServerClient {

    @GetMapping("/server")
    String get();

    @PostMapping("/server")
    String post();
}
