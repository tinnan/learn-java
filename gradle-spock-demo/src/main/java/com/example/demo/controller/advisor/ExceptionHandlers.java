package com.example.demo.controller.advisor;

import com.example.demo.controller.exception.IneligibleCustomerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {
    public static final int HTTP_STATUS_INELIGIBLE_CUSTOMER = 499;

    @ExceptionHandler(IneligibleCustomerException.class)
    public ResponseEntity<Void> handlerIneligibleCustomer(IneligibleCustomerException ex) {
        return ResponseEntity.status(HTTP_STATUS_INELIGIBLE_CUSTOMER).build();
    }
}
