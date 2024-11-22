package com.example.demo.requestscope.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepAsyncService {

    private final HttpHeaders httpHeaders;

    @Async
    public void runDeepTaskVoid() {
        log.info("runDeepTaskVoid - Request scope bean: {}", httpHeaders);
    }
}
