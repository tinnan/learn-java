package com.example.demo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fraud", url = "${feign.client.fraud.url}")
public interface FraudClient {
    @GetMapping("/check")
    boolean isFraudster(@RequestParam("customerEmail") String customerEmail);
}
