package com.example.demo.requestscope.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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
    private final DeepAsyncService deepAsyncService;

    @Async
    public void runTaskVoid() {
        sleep();
        log.info("runTaskVoid - Request scope bean: {}", httpHeaders);
    }

    @Async
    public CompletableFuture<Void> runTaskCompletableFutureVoid() {
        sleep();
        log.info("runTaskCompletableFutureVoid - Request scope bean: {}", httpHeaders);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<String> runTaskCompletableFutureString() {
        sleep();
        log.info("runTaskCompletableFutureString - Request scope bean: {}", httpHeaders);
        return CompletableFuture.completedFuture(httpHeaders.toString());
    }

    @Async
    public void runDeepTaskVoid() {
        sleep();
        log.info("runDeepTaskVoid - Request scope bean: {}", httpHeaders);
        deepAsyncService.runDeepTaskVoid();
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            log.info("Interrupted");
        }
    }
}
