package com.example.demo.async.customer;

import com.example.demo.async.api.CustomerCreateApi;
import com.example.demo.async.model.CustomerCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomerCreateController implements CustomerCreateApi {

    @Override
    public CustomerCreateResponse createCustomer(HttpHeaders headers, String customerEmail) {
        int newCustomerId = 1;
        log.info("Customer created for email {} - new ID {}", customerEmail, newCustomerId);
        return new CustomerCreateResponse(newCustomerId);
    }
}
