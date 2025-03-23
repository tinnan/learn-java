package com.example.demo.async.asyncwrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OutboundAsyncWrapperService {

    @Async
    public <R> CompletableFuture<R> wrap(Supplier<R> supplier) {
        return CompletableFuture.completedFuture(supplier.get());
    }

    @Async
    @SafeVarargs
    public final CompletableFuture<AsyncResult> allOf(Supplier<Object>... suppliers) throws ExecutionException {
        CompletableFuture<?>[] completableFutures = Arrays.stream(suppliers).map(this::wrap)
            .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).join();
        List<Object> resultList = new ArrayList<>();
        for (CompletableFuture<?> completableFuture : completableFutures) {
            try {
                resultList.add(completableFuture.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return CompletableFuture.completedFuture(new AsyncResult(resultList));
    }

    @SuppressWarnings("unchecked")
    @AllArgsConstructor
    public static class AsyncResult {

        private final List<Object> resultList;

        public <R> R get(int index) {
            return (R) this.resultList.get(index);
        }
    }
}
