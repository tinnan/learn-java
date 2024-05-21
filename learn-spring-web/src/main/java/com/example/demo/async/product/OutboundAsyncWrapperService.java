package com.example.demo.async.product;

import com.example.demo.async.clients.CustomerClient;
import com.example.demo.async.clients.FraudClient;
import com.example.demo.async.model.CustomerInfoResponse;
import com.example.demo.async.model.FraudCheckResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboundAsyncWrapperService {
    private final CustomerClient customerClient;
    private final FraudClient fraudClient;
    @Async
    public CompletableFuture<CustomerInfoResponse> getCustomerInfo(Integer customerId) {
        CustomerInfoResponse customerInfo = customerClient.getCustomerInfo(customerId);
        return CompletableFuture.completedFuture(customerInfo);
    }

    @Async
    public CompletableFuture<FraudCheckResponse> isFraudster(Integer customerId) {
        FraudCheckResponse fraudster = fraudClient.isFraudster(customerId);
        return CompletableFuture.completedFuture(fraudster);
    }
}
