package com.example.demo.async.product;

import com.example.demo.async.model.CustomerInfoResponse;
import com.example.demo.async.model.FraudCheckResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final OutboundAsyncWrapperService wrapperService;

    public void apply(Integer customerId, String productId) {

        try {
            long startTs = System.currentTimeMillis();
            CompletableFuture<CustomerInfoResponse> customerInfoFuture = wrapperService.getCustomerInfo(customerId);
            CompletableFuture<FraudCheckResponse> fraudsterFuture = wrapperService.isFraudster(customerId);
            CompletableFuture.allOf(customerInfoFuture, fraudsterFuture).thenRun(() -> {
                long elapsed = System.currentTimeMillis() - startTs;
                log.info("Async API call elapsed: {} ms", elapsed);
            });
            CustomerInfoResponse customerInfoResponse = customerInfoFuture.get();
            FraudCheckResponse fraudCheckResponse = fraudsterFuture.get();
            if (!fraudCheckResponse.isFraudster()) {
                log.info("Product application successful for customer {} {}", customerInfoResponse.firstName(),
                    customerInfoResponse.lastName());
            } else {
                log.info("Customer ID {} is not eligible to apply for product {}", customerId, productId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
