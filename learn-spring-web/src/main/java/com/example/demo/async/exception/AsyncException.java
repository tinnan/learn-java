package com.example.demo.async.exception;

import com.example.demo.async.model.AsyncResult;
import java.io.Serial;
import java.util.List;
import lombok.Getter;

@Getter
public class AsyncException extends Exception {

    @Serial
    private static final long serialVersionUID = -631113485611541029L;
    private final List<AsyncResult> results;

    public AsyncException(List<AsyncResult> results, Throwable cause) {
        super(cause);
        this.results = results;
    }
}
