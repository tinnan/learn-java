package com.example.demo.async.api;

import com.example.demo.async.model.FraudCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface FraudApi {

    @GetMapping("/api/v1/check-fraud")
    FraudCheckResponse isFraudster(@RequestHeader HttpHeaders headers, @RequestParam("customerId") Integer customerId);
}
