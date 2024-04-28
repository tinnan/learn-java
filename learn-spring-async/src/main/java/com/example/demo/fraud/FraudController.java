package com.example.demo.fraud;

import com.example.demo.model.FraudCheckResponse;
import com.example.demo.clients.FraudClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FraudController implements FraudClient {
    @Value("${service.fraud.response.delay-ms}")
    private Integer delay;

    @Override
    public FraudCheckResponse isFraudster(Integer customerId) {
        try {
            Thread.sleep(delay);
            return new FraudCheckResponse(false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
