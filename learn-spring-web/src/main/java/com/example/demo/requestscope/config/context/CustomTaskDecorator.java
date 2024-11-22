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
                /*
                Call setRequestAttributes to stop Spring from throwing
                java.lang.IllegalStateException - No thread-bound request found.
                 */
                RequestContextHolder.setRequestAttributes(new NoOpRequestAttributes());
                HttpHeadersContextHolder.set(httpHeaders);
                runnable.run();
            } finally {
                log.info("Clean up context");
                RequestContextHolder.resetRequestAttributes();
                HttpHeadersContextHolder.remove();
            }
        };
    }
}
