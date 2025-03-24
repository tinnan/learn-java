package com.example.demo.async.service;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HandleAsyncTestService {

    public String succeed(String text) throws InterruptedException {
        log.info("succeed(String) - sleep");
        TimeUnit.SECONDS.sleep(1);
        log.info("succeed(String) - awake");
        return "Result of succeed(%s)".formatted(text);
    }

    public String failAfter1Second() throws InterruptedException {
        log.info("failAfter1Second() - sleep");
        TimeUnit.SECONDS.sleep(1);
        log.info("failAfter1Second() - awake");
        throw new IllegalArgumentException("From failAfter1Second()");
    }

    public String failAfter2Second() throws InterruptedException {
        log.info("failAfter2Second() - sleep");
        TimeUnit.SECONDS.sleep(2);
        log.info("failAfter2Second() - awake");
        throw new IllegalStateException("From failAfter2Second()");
    }

    public void succeedVoid() throws InterruptedException {
        log.info("succeedVoid() - sleep");
        TimeUnit.SECONDS.sleep(1);
        log.info("succeedVoid() - awake");
    }

    public SuccessResponse succeed() throws InterruptedException {
        log.info("succeed() - sleep");
        TimeUnit.SECONDS.sleep(1);
        log.info("succeed() - awake");
        return SuccessResponse.builder().name("John").build();
    }

    @Getter
    @Builder
    public static class SuccessResponse {

        private String name;
    }
}
