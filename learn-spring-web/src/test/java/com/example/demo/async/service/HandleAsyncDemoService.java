package com.example.demo.async.service;

import com.example.demo.async.asyncwrapper.OutboundAsyncWrapperService;
import com.example.demo.async.exception.AsyncAllOfException;
import com.example.demo.async.model.AsyncResult;
import com.example.demo.async.model.Tuple4;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleAsyncDemoService {

    private final OutboundAsyncWrapperService outboundAsyncWrapperService;

    public DemoResponse runFlow(String api1Id, int api4CustomerStatus) {
        try {
            Tuple4<Api1Response, Void, Api3Response, Api4Response> result = OutboundAsyncWrapperService.all(
                outboundAsyncWrapperService.wrap(() -> getDataFromApi1(api1Id)),
                outboundAsyncWrapperService.wrap(this::getDataFromApi2),
                outboundAsyncWrapperService.wrap(this::getDataFromApi3),
                outboundAsyncWrapperService.wrap(
                    () -> getDataFromApi4(Api4Request.builder().customerStatus(api4CustomerStatus).build()))
            );
            return DemoResponse.builder()
                .api1(result.getT1())
                .api3(result.getT3())
                .api4(result.getT4())
                .build();
        } catch (AsyncAllOfException e) {
            // Handle error from each async process (if you need to).
            e.getResults().stream().filter(AsyncResult::isError).forEach(ar -> {
                log.error("Async error message: {}", ar.getError().getMessage());
            });
            // AsyncAllOfException.getCause() returns first error found from async process results
            // (ordered sequentially by input of method OutboundAsyncWrapperService.all)
            if (e.getCause() instanceof IllegalArgumentException ex) {
                throw ex;
            }
            throw new IllegalStateException(e.getCause());
        }
    }

    private Api1Response getDataFromApi1(String id) {
        try {
            TimeUnit.SECONDS.sleep(1);
            return Api1Response.builder().transactionId("api-1-txn-" + id).build();
        } catch (InterruptedException e) {
            throw new RuntimeException("Handled InterruptedException from API 1", e);
        }
    }

    private void getDataFromApi2() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException("Handled InterruptedException from API 2", e);
        }
    }

    private Api3Response getDataFromApi3() {
        try {
            TimeUnit.SECONDS.sleep(1);
            return Api3Response.builder().transactionId("api-3-txn").build();
        } catch (InterruptedException e) {
            throw new RuntimeException("Handled InterruptedException from API 3", e);
        }
    }

    private Api4Response getDataFromApi4(Api4Request request) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException("Handled InterruptedException from API 3", e);
        }
        if (request.customerStatus == 10) {
            throw new IllegalArgumentException("API 4 - Invalid customer status");
        }
        return Api4Response.builder().transactionId("api-4-txn").build();
    }

    @Getter
    @Builder
    public static class Api1Response {

        private String transactionId;
    }

    @Getter
    @Builder
    public static class Api3Response {

        private String transactionId;
    }

    @Getter
    @Builder
    public static class Api4Request {

        private Integer customerStatus;
    }

    @Getter
    @Builder
    public static class Api4Response {

        private String transactionId;
    }

    @Getter
    @Builder
    public static class DemoResponse {

        private Api1Response api1;
        private Api3Response api3;
        private Api4Response api4;
    }
}
