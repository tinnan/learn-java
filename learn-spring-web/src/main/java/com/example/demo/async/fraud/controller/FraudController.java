package com.example.demo.async.fraud.controller;

import com.example.demo.async.api.FraudApi;
import com.example.demo.async.fraud.model.FraudCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FraudController implements FraudApi {

    @Value("${service.fraud.response.delay-ms}")
    private Integer delay;

    @Override
    public FraudCheckResponse isFraudster(HttpHeaders headers, Integer customerId) {
        try {
            Thread.sleep(delay);
            return new FraudCheckResponse(false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
