package com.example.demo.controller.advice;

import com.example.demo.controller.exception.CommonException;
import com.example.demo.controller.exception.ContentNotAllowedException;
import com.example.demo.controller.exception.UserNotFoundException;
import com.example.demo.domain.ApiError;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

@ControllerAdvice({"com.example.demo.controller"})
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, ContentNotAllowedException.class, CommonException.class,
        Exception.class})
    public ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        log.error("Handling exception: {}", ex.getClass().getName());
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof UserNotFoundException unfe) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleUserNotFoundException(unfe, headers, status, request);
        } else if (ex instanceof ContentNotAllowedException cnae) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleContentNotAllowedException(cnae, headers, status, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }

    private ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException unfe,
        HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        List<String> errors = Collections.singletonList(unfe.getMessage());
        return handleExceptionInternal(unfe, new ApiError(errors), headers, status, request);
    }

    public ResponseEntity<ApiError> handleContentNotAllowedException(ContentNotAllowedException cnae,
        HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        List<String> errorMessages =
            cnae.getErrors()
                .stream()
                .map(contentError -> contentError.getObjectName() + " " + contentError.getDefaultMessage())
                .toList();

        return handleExceptionInternal(cnae, new ApiError(errorMessages), headers, status, request);
    }

    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
