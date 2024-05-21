package com.example.demo.async.customer;

import com.example.demo.async.clients.CustomerClient;
import com.example.demo.async.model.CustomerInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CustomerController implements CustomerClient {

    @Value("${service.customer.response.delay-ms}")
    private Integer delay;

    @Override
    public CustomerInfoResponse getCustomerInfo(Integer customerId) {
        try {
            Thread.sleep(delay);
            return new CustomerInfoResponse(customerId, "John", "Doe", "john.d@gmail.com");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
