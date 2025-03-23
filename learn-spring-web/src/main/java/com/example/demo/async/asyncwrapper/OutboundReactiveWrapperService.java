package com.example.demo.async.asyncwrapper;

import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class OutboundReactiveWrapperService {

    public <R> Mono<R> wrap(Supplier<R> supplier) {
        return Mono.fromCallable(supplier::get).subscribeOn(Schedulers.boundedElastic());
    }
}
