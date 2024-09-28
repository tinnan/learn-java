package com.example.demo.async.api;

import com.example.demo.async.model.CustomerInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface CustomerApi {

    @GetMapping("/api/v1/customer/{customerId}")
    CustomerInfoResponse getCustomerInfo(@RequestHeader HttpHeaders headers,
        @PathVariable("customerId") Integer customerId);
}
