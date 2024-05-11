package com.example.demo.controller;

import com.example.demo.clients.CustomerClient;
import com.example.demo.domain.Customer;
import com.example.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerClient {
    private final CustomerService customerService;

    @Override
    public Customer register(Customer customer) {
        return customerService.register(customer);
    }
}
