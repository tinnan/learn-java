package com.example.demo.async.product;

import com.example.demo.async.clients.CustomerClient;
import com.example.demo.async.clients.FraudClient;
import com.example.demo.async.model.CustomerInfoResponse;
import com.example.demo.async.model.FraudCheckResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboundAsyncWrapperService {

    private final CustomerClient customerClient;
    private final FraudClient fraudClient;

    @Async
    public CompletableFuture<CustomerInfoResponse> getCustomerInfo(HttpHeaders headers, Integer customerId) {
        CustomerInfoResponse customerInfo = customerClient.getCustomerInfo(headers, customerId);
        return CompletableFuture.completedFuture(customerInfo);
    }

    @Async
    public CompletableFuture<FraudCheckResponse> isFraudster(HttpHeaders headers, Integer customerId) {
        FraudCheckResponse fraudster = fraudClient.isFraudster(headers, customerId);
        return CompletableFuture.completedFuture(fraudster);
    }
}
