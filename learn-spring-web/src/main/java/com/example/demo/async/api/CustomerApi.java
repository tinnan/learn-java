package com.example.demo.async.api;

import com.example.demo.async.model.CustomerInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Customer API")
public interface CustomerApi {

    @Operation(summary = "Get customer information")
    @GetMapping("/customer/{customerId}")
    CustomerInfoResponse getCustomerInfo(@RequestHeader HttpHeaders headers,
        @PathVariable("customerId") Integer customerId);
}
