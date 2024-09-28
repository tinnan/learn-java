package com.example.demo.async.customer.controller;

import com.example.demo.async.api.CustomerInfoApi;
import com.example.demo.async.customer.model.CustomerInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomerInfoController implements CustomerInfoApi {

    @Value("${service.customer.response.delay-ms}")
    private Integer delay;

    @Override
    public CustomerInfoResponse getCustomerInfo(HttpHeaders headers, Integer customerId) {
        try {
            Thread.sleep(delay);
            return new CustomerInfoResponse(customerId, "John", "Doe", "john.d@gmail.com");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
