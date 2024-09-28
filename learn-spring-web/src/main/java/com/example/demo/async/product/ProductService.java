package com.example.demo.async.product;

import com.example.demo.async.clients.CustomerCreateClient;
import com.example.demo.async.model.CustomerCreateResponse;
import com.example.demo.async.model.CustomerInfoResponse;
import com.example.demo.async.model.FraudCheckResponse;
import com.example.demo.async.service.OutboundAsyncWrapperService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final CustomerCreateClient customerCreateClient;
    private final OutboundAsyncWrapperService wrapperService;

    public void apply(HttpHeaders headers, String customerEmail, String productId) {

        try {
            long startTs = System.currentTimeMillis();
            CustomerCreateResponse customer = customerCreateClient.createCustomer(headers, customerEmail);
            Integer customerId = customer.customerId();
            CompletableFuture<CustomerInfoResponse> customerInfoFuture = wrapperService.getCustomerInfo(headers,
                customerId);
            CompletableFuture<FraudCheckResponse> fraudsterFuture = wrapperService.isFraudster(headers, customerId);
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
