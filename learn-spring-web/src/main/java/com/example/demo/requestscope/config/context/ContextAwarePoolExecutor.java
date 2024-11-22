package com.example.demo.requestscope.config.context;

import java.io.Serial;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
public class ContextAwarePoolExecutor extends ThreadPoolTaskExecutor {

    @Serial
    private static final long serialVersionUID = -9159665732983345633L;

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        log.info("Future<T> - submit - Callable<T>");
        return super.submit(new ContextAwareCallable<>(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        log.info("Future<?> - submit - Runnable");
        return super.submit(new ContextAwareRunnable(task));
    }

    @Override
    public <T> CompletableFuture<T> submitCompletable(Callable<T> task) {
        log.info("CompletableFuture<T> - submitCompletable - Callable<T>");
        return super.submitCompletable(
            new ContextAwareCallable<>(task));
    }

    @Override
    public CompletableFuture<Void> submitCompletable(Runnable task) {
        log.info("CompletableFuture<Void> - submitCompletable - Runnable");
        return super.submitCompletable(new ContextAwareRunnable(task));
    }

}
