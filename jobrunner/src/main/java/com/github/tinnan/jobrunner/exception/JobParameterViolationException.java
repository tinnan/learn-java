package com.github.tinnan.jobrunner.exception;

import java.io.Serial;

public class JobParameterViolationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8186909826346933707L;

    public JobParameterViolationException(String message) {
        super(message);
    }
}
