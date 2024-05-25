package org.example.security.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.security.exception.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class SecurityExceptionHandler {
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(403).body(ex.getMessage());
    }
}
