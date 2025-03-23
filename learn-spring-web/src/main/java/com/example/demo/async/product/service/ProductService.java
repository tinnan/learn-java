package com.example.demo.async.product.service;

import com.example.demo.async.asyncwrapper.OutboundAsyncWrapperService;
import com.example.demo.async.asyncwrapper.OutboundReactiveWrapperService;
import com.example.demo.async.clients.CustomerCreateClient;
import com.example.demo.async.clients.CustomerInfoClient;
import com.example.demo.async.clients.FraudClient;
import com.example.demo.async.constant.ConcurrencyMode;
import com.example.demo.async.customer.model.CustomerCreateRequest;
import com.example.demo.async.customer.model.CustomerCreateResponse;
import com.example.demo.async.customer.model.CustomerInfoResponse;
import com.example.demo.async.fraud.model.FraudCheckResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    @Value("${com.example.demo.async.concurrency-mode}")
    private final ConcurrencyMode concurrencyMode;
    private final HttpHeaders httpHeaders;
    private final CustomerInfoClient customerInfoClient;
    private final CustomerCreateClient customerCreateClient;
    private final FraudClient fraudClient;
    private final OutboundAsyncWrapperService outboundAsyncWrapperService;
    private final OutboundReactiveWrapperService outboundReactiveWrapperService;

    public void apply(String customerEmail, String productId) {

        try {
            FlowResult flowResult = executeFlow(customerEmail);
            Integer customerId = flowResult.customerId();
            CustomerInfoResponse customerInfoResponse = flowResult.customerInfoResponse();
            FraudCheckResponse fraudCheckResponse = flowResult.fraudCheckResponse();
            if (!Boolean.TRUE.equals(fraudCheckResponse.isFraudster())) {
                log.debug("Product application successful for customer {} {}", customerInfoResponse.firstName(),
                    customerInfoResponse.lastName());
            } else {
                log.debug("Customer ID {} is not eligible to apply for product {}", customerId, productId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FlowResult executeFlow(String customerEmail) throws ExecutionException, InterruptedException {
        if (concurrencyMode == ConcurrencyMode.ASYNC) {
            return executeAsyncFlow(customerEmail);
        } else {
            return executeReactiveFlow(customerEmail);
        }
    }

    private FlowResult executeAsyncFlow(String customerEmail) throws ExecutionException, InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CustomerCreateResponse customer = customerCreateClient.createCustomer(httpHeaders,
            CustomerCreateRequest.builder().customerEmail(customerEmail).build());
        Integer customerId = customer.getCustomerId();
        CompletableFuture<CustomerInfoResponse> customerInfoFuture = outboundAsyncWrapperService.wrap(
            () -> customerInfoClient.getCustomerInfo(httpHeaders, customerId));
        CompletableFuture<FraudCheckResponse> fraudsterFuture = outboundAsyncWrapperService.wrap(
            () -> fraudClient.isFraudster(httpHeaders, customerId));
        CompletableFuture.allOf(customerInfoFuture, fraudsterFuture).thenRun(() -> {
            stopWatch.stop();
            log.info("Async API call elapsed: {} ms", stopWatch.getTotalTimeMillis());
        });
        return new FlowResult(customerId, customerInfoFuture.get(), fraudsterFuture.get());
    }

    private FlowResult executeReactiveFlow(String customerEmail) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            CustomerCreateResponse customer = customerCreateClient.createCustomer(httpHeaders,
                CustomerCreateRequest.builder().customerEmail(customerEmail).build());
            Integer customerId = customer.getCustomerId();
            Mono<CustomerInfoResponse> customerInfoFuture = outboundReactiveWrapperService.wrap(
                () -> customerInfoClient.getCustomerInfo(httpHeaders, customerId));
            Mono<FraudCheckResponse> fraudsterFuture = outboundReactiveWrapperService.wrap(
                () -> fraudClient.isFraudster(httpHeaders, customerId));
            Tuple2<CustomerInfoResponse, FraudCheckResponse> resolution = Mono.zip(customerInfoFuture, fraudsterFuture)
                .block();
            if (resolution == null) {
                throw new IllegalStateException("Did not expect NULL here");
            }
            return new FlowResult(customerId, resolution.getT1(), resolution.getT2());
        } finally {
            stopWatch.stop();
            log.info("Reactive API call elapsed: {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    record FlowResult(
        Integer customerId,
        CustomerInfoResponse customerInfoResponse,
        FraudCheckResponse fraudCheckResponse
    ) {

    }
}
