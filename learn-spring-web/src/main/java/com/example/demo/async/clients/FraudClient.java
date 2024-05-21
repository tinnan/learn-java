package com.example.demo.async.clients;

import com.example.demo.async.model.FraudCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fraud", url = "${clients.fraud.url}")
public interface FraudClient {
    @GetMapping("/api/v1/check-fraud")
    FraudCheckResponse isFraudster(@RequestParam("customerId") Integer customerId);
}
