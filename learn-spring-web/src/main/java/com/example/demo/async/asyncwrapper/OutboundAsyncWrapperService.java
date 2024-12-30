package com.example.demo.async.asyncwrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OutboundAsyncWrapperService {

    @Async
    public <R> CompletableFuture<R> wrap(Supplier<R> supplier) {
        return CompletableFuture.completedFuture(supplier.get());
    }
}
