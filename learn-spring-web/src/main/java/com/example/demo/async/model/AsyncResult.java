package com.example.demo.async.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("unchecked")
@Getter
@AllArgsConstructor
public class AsyncResult {

    private Object data;
    private Exception error;

    public boolean isError() {
        return this.error != null;
    }

    public <T> T getData() {
        return (T) this.data;
    }
}
