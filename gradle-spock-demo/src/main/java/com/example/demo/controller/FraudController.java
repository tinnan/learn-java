package com.example.demo.controller;

import com.example.demo.clients.FraudClient;
import com.example.demo.service.FraudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController implements FraudClient {
    private final FraudService fraudService;

    @Override
    public boolean isFraudster(String customerEmail) {
        return fraudService.isFraudster(customerEmail);
    }
}
