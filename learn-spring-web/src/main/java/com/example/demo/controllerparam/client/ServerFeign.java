package com.example.demo.controllerparam.client;

import com.example.demo.controllerparam.config.FeignConfig;
import com.example.demo.controllerparam.config.FeignConfig2;
import com.example.demo.controllerparam.controller.DemoController.GetParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "server", url = "${feign.server-client.url}",
    // ! If there is same bean type in both config, the last one added will take effect.
    configuration = {FeignConfig2.class, FeignConfig.class})
public interface ServerFeign {

    @GetMapping("/server")
    GetParams serverGet(@RequestHeader HttpHeaders headers, GetParams params);
}
