package com.example.demo.customer;

import com.example.demo.clients.CustomerClient;
import com.example.demo.model.CustomerInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CustomerController implements CustomerClient {

    @Override
    public CustomerInfoResponse getCustomerInfo(Integer customerId) {
        try {
            Thread.sleep(1000);
            return new CustomerInfoResponse(customerId, "John", "Doe", "john.d@gmail.com");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
