package com.example.demo.async.customer.controller;

import com.example.demo.async.api.CustomerCreateApi;
import com.example.demo.async.customer.model.CustomerCreateRequest;
import com.example.demo.async.customer.model.CustomerCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomerCreateController implements CustomerCreateApi {

    @Override
    public CustomerCreateResponse createCustomer(HttpHeaders headers, CustomerCreateRequest request) {
        int newCustomerId = 1;
        log.debug("Customer created for email {} - new ID {}", request.getCustomerEmail(), newCustomerId);
        return CustomerCreateResponse.builder().customerId(newCustomerId).build();
    }
}
