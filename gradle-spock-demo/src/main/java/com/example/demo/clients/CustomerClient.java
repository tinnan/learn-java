package com.example.demo.clients;

import com.example.demo.domain.Customer;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "customer", url = "http://localhost:8080")
@RequestMapping("/api/v1/customer")
public interface CustomerClient {
    @PostMapping
    Customer register(@Valid @RequestBody Customer customer);
}
