package com.github.tinnan.jobrunner.advice;

import com.github.tinnan.jobrunner.model.ErrorResponse;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobExecutionException.class)
    public ResponseEntity<ErrorResponse> handle(JobExecutionException e) {
        if (e instanceof JobInstanceAlreadyCompleteException) {
            return ResponseEntity.badRequest()
                .body(ErrorResponse.builder().message("This job is already complete").build());
        } else if (e instanceof JobExecutionAlreadyRunningException) {
            return ResponseEntity.badRequest()
                .body(ErrorResponse.builder().message("This job is still running").build());
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder().message(e.getMessage()).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder().message(e.getMessage()).build();
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
