package com.example.demo.requestscope.service;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncService {

    // Request scope bean.
    private final HttpHeaders httpHeaders;

    @Async
    public void runTaskVoid() {
        log.info("runTaskVoid - Request scope bean: {}", httpHeaders);
    }

    @Async
    public CompletableFuture<Void> runTaskCompletableFutureVoid() {
        log.info("runTaskCompletableFutureVoid - Request scope bean: {}", httpHeaders);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<String> runTaskCompletableFutureString() {
        log.info("runTaskCompletableFutureString - Request scope bean: {}", httpHeaders);
        return CompletableFuture.completedFuture(httpHeaders.toString());
    }
}
