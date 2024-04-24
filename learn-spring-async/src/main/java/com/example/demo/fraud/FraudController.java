package com.example.demo.fraud;

import com.example.demo.model.FraudCheckResponse;
import com.example.demo.clients.FraudClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FraudController implements FraudClient {

    @Override
    public FraudCheckResponse isFraudster(Integer customerId) {
        try {
            Thread.sleep(3000);
            return new FraudCheckResponse(false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
