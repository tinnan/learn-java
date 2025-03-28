package com.example.demo.async.service;

import static com.example.demo.async.constant.Constants.HEADER_X_CORRELATION_ID;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestScopeBeanInAsyncTestService {

    private static final AtomicReference<String> store = new AtomicReference<>("init");
    private final HttpHeaders httpHeaders;

    @Async
    @SneakyThrows
    public CompletableFuture<Void> execute() {
        String correlationId = httpHeaders.getFirst(HEADER_X_CORRELATION_ID);
        log.info("Request x-correlation-id {}", correlationId);
        TimeUnit.SECONDS.sleep(1);
        return CompletableFuture.runAsync(() -> setStorageValue(correlationId));
    }

    public static void setStorageValue(String s) {
        synchronized (store) {
            store.set(s);
            store.notifyAll();
        }
    }

    public static String getStorageValue() {
        return store.get();
    }

    public static void resetStorage() {
        store.set("init");
    }

    public static void waitForSetStorageValue(long millis) throws InterruptedException {
        synchronized (store) {
            store.wait(millis);
        }
    }
}
