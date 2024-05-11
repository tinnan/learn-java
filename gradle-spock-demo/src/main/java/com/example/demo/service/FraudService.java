package com.example.demo.service;

import com.example.demo.domain.FraudRecord;
import com.example.demo.repo.FraudCheckRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudService {
    private final FraudCheckRepository fraudCheckRepository;

    public boolean isFraudster(String customerEmail) {
        Optional<FraudRecord> byCustomerId = fraudCheckRepository.findByCustomerEmail(customerEmail);
        if (byCustomerId.isEmpty()) return false;
        return byCustomerId.get().getFraudster();
    }
}
