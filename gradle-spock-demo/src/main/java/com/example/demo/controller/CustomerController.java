package com.example.demo.controller;

import com.example.demo.clients.CustomerClient;
import com.example.demo.domain.Customer;
import com.example.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController implements CustomerClient {
    private final CustomerService customerService;

    @Override
    @SneakyThrows
    public Customer register(Customer customer) {
        log.info("Registering customer.");
        return customerService.register(customer);
    }

    @Override
    public Customer get(long customerId) {
        return customerService.find(customerId);
    }
}
