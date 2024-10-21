package com.example.demo.controllerparam.client;

import com.example.demo.controllerparam.config.FeignConfig;
import com.example.demo.controllerparam.controller.DemoServerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "server", url = "${feign.server-client.url}",
    // ! If there is same bean type in both config, the last one added will take effect.
    configuration = FeignConfig.class)
public interface ServerFeign extends DemoServerApi {

}
