package com.github.tinnan.jobrunner.advice;

import com.github.tinnan.jobrunner.exception.JobParameterNotFoundException;
import com.github.tinnan.jobrunner.exception.JobParameterViolationException;
import com.github.tinnan.jobrunner.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
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
            return handle((Exception) e);
        }
    }

    @ExceptionHandler(JobParameterNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(JobParameterNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(JobParameterViolationException.class)
    public ResponseEntity<ErrorResponse> handle(JobParameterViolationException e) {
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        log.error("Unexpected error occurred", e);
        ErrorResponse errorResponse = ErrorResponse.builder().message("Unexpected error occurred").build();
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
