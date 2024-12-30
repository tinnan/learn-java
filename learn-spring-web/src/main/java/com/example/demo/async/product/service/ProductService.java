package com.example.demo.async.product.service;

import com.example.demo.async.asyncwrapper.OutboundAsyncWrapperService;
import com.example.demo.async.clients.CustomerCreateClient;
import com.example.demo.async.clients.CustomerInfoClient;
import com.example.demo.async.clients.FraudClient;
import com.example.demo.async.customer.model.CustomerCreateRequest;
import com.example.demo.async.customer.model.CustomerCreateResponse;
import com.example.demo.async.customer.model.CustomerInfoResponse;
import com.example.demo.async.fraud.model.FraudCheckResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final CustomerInfoClient customerInfoClient;
    private final CustomerCreateClient customerCreateClient;
    private final FraudClient fraudClient;
    private final OutboundAsyncWrapperService wrapperService;

    public void apply(String customerEmail, String productId) {

        try {
            long startTs = System.currentTimeMillis();
            CustomerCreateResponse customer = customerCreateClient.createCustomer(null,
                CustomerCreateRequest.builder().customerEmail(customerEmail).build());
            Integer customerId = customer.getCustomerId();
            CompletableFuture<CustomerInfoResponse> customerInfoFuture = wrapperService.wrap(
                () -> customerInfoClient.getCustomerInfo(null, customerId));
            CompletableFuture<FraudCheckResponse> fraudsterFuture = wrapperService.wrap(
                () -> fraudClient.isFraudster(null, customerId));
            CompletableFuture.allOf(customerInfoFuture, fraudsterFuture).thenRun(() -> {
                long elapsed = System.currentTimeMillis() - startTs;
                log.info("Async API call elapsed: {} ms", elapsed);
            });
            CustomerInfoResponse customerInfoResponse = customerInfoFuture.get();
            FraudCheckResponse fraudCheckResponse = fraudsterFuture.get();
            if (!Boolean.TRUE.equals(fraudCheckResponse.isFraudster())) {
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
