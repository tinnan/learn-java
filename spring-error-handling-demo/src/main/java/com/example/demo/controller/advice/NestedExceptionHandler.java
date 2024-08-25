package com.example.demo.controller.advice;

import com.example.demo.controller.exception.NestedCauseHandlingTestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Order(1)
@ControllerAdvice
public class NestedExceptionHandler {

    @ExceptionHandler({NestedCauseHandlingTestException.class})
    public ResponseEntity<String> handleException(NestedCauseHandlingTestException ex) {
        log.error("Handling nested exception: {}", ex.getClass().getName());
        return ResponseEntity.status(500).body("Nested exception handled");
    }
}
