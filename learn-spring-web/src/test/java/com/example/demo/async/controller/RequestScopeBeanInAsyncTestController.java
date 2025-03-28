package com.example.demo.async.controller;

import com.example.demo.async.service.RequestScopeBeanInAsyncTestService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/async/request-scope-bean")
@RequiredArgsConstructor
public class RequestScopeBeanInAsyncTestController {

    private final RequestScopeBeanInAsyncTestService service;

    @PostMapping("/wait")
    public void asyncWait() {
        log.info("Executing /async/request-scope-bean/wait");
        service.execute().join();
    }

    @PostMapping("/no-wait")
    public void asyncNoWait() {
        service.execute();
    }
}
