package com.github.tinnan.jobrunner.exception;

import java.io.Serial;

public class JobParameterNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2610030583070356683L;

    public JobParameterNotFoundException(String parameterName) {
        super("Not found job parameter: " + parameterName);
    }
}
