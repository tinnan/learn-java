package com.example.demo.clients;

import com.example.demo.domain.Customer;
import com.example.demo.domain.CustomerRegisterResult;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer", url = "${feign.client.customer.url}")
public interface CustomerClient {
    @PostMapping
    CustomerRegisterResult register(@Valid @RequestBody Customer customer);
    @GetMapping
    Customer get(@RequestParam("customerId") long customerId);
}
