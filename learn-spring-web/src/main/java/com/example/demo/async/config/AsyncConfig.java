package com.example.demo.async.config;

import com.example.demo.async.constant.Constants;
import com.example.demo.requestscope.config.context.NoOpRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setTaskDecorator(new CustomThreadDecorator());
        executor.initialize();
        return executor;
    }

    @Bean
    @RequestScope
    public HttpHeaders httpHeaders() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (Objects.nonNull(requestAttributes) && requestAttributes instanceof ServletRequestAttributes attrs) {
            HttpServletRequest curRequest = attrs.getRequest();
            String correlationId = curRequest.getHeader(Constants.HEADER_X_CORRELATION_ID);
            if (StringUtils.isNotBlank(correlationId)) {
                httpHeaders.set(Constants.HEADER_X_CORRELATION_ID, correlationId);
            }
        } else {
            httpHeaders.set(Constants.HEADER_X_CORRELATION_ID, MDC.get(Constants.HEADER_X_CORRELATION_ID));
        }
        return httpHeaders;
    }

    static class CustomThreadDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    MDC.setContextMap(contextMap);
                    /*
                    Call setRequestAttributes to stop Spring from throwing
                    "java.lang.IllegalStateException - No thread-bound request found" when trying to access
                    request scoped bean in non request-bound thread.
                     */
                    RequestContextHolder.setRequestAttributes(new NoOpRequestAttributes());
                    runnable.run();
                } finally {
                    MDC.clear();
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }
    }
}