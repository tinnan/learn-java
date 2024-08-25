package com.example.demo.controller;

import com.example.demo.controller.exception.CommonException;
import com.example.demo.controller.exception.NestedCauseHandlingTestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NestedExceptionHandlingController {

    @GetMapping("/exception/nested")
    public ResponseEntity<String> get() {
        /*
            NestedCauseHandlingTestException is handled by NestedExceptionHandler
            despite CommonException is being handled by GlobalExceptionHandler
            since NestedExceptionHandler is annotated with @Order(1).
         */
        throw new CommonException(new NestedCauseHandlingTestException());
    }
}
