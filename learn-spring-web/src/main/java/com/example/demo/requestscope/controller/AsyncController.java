package com.example.demo.requestscope.controller;

import com.example.demo.requestscope.service.AsyncService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AsyncController {

    private final HttpHeaders httpHeaders;
    private final AsyncService asyncService;

    @PostMapping("/task/run/void")
    public void runTaskVoid() {
        log.info("runTaskVoid - HttpHeaders bean: {}", httpHeaders);
        asyncService.runTaskVoid();
    }

    @PostMapping("/task/run/completable/future/void")
    public void runTaskCompletableFutureVoid() {
        CompletableFuture<Void> future = asyncService.runTaskCompletableFutureVoid();
        future.thenRun(() -> log.info("runTaskCompletableFutureVoid - done"));
    }

    @PostMapping("/task/run/completable/future/string")
    public String runTaskCompletableFutureString() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = asyncService.runTaskCompletableFutureString();
        CompletableFuture.allOf(future);
        return future.get();
    }
}
