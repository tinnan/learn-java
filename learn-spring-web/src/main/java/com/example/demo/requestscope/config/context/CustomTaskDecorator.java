package com.example.demo.requestscope.config.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
public class CustomTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        log.info("Decorating task");
        HttpHeaders httpHeaders = ContextUtils.getHttpHeaders();
        return () -> {
            log.info("Execute decorator");
            try {
                RequestContextHolder.setRequestAttributes(new NoOpRequestAttributes());
                HttpHeadersContextHolder.set(httpHeaders);
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                HttpHeadersContextHolder.remove();
            }
        };
    }
}
