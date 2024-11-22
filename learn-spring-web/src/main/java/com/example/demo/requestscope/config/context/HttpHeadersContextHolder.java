package com.example.demo.requestscope.config.context;

import org.springframework.http.HttpHeaders;

public final class HttpHeadersContextHolder {

    private static final ThreadLocal<HttpHeaders> httpHeadersHolder = new ThreadLocal<>();

    public static void set(HttpHeaders headers) {
        httpHeadersHolder.set(headers);
    }

    public static HttpHeaders get() {
        return httpHeadersHolder.get();
    }

    public static void remove() {
        httpHeadersHolder.remove();
    }

    private HttpHeadersContextHolder() {}
}
