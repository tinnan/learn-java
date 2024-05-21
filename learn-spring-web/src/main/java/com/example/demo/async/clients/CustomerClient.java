package com.example.demo.async.clients;

import com.example.demo.async.model.CustomerInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer", url = "${clients.customer.url}")
public interface CustomerClient {
    @GetMapping("/api/v1/customer/{customerId}")
    CustomerInfoResponse getCustomerInfo(@PathVariable("customerId") Integer customerId);
}
