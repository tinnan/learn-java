package com.example.demo.async.asyncwrapper;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class OutboundReactiveWrapperService {

    private final TaskExecutor taskExecutor;

    public <R> Mono<R> wrap(Supplier<R> supplier) {
        return Mono.fromCallable(supplier::get).subscribeOn(Schedulers.fromExecutor(taskExecutor));
    }
}
