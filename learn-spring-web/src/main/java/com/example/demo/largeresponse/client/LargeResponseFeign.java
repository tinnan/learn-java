package com.example.demo.largeresponse.client;

import com.example.demo.largeresponse.model.response.LargeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "feign.large-response", url = "${feign.large-response.url}")
public interface LargeResponseFeign {

    @GetMapping
    LargeResponse get();
}
