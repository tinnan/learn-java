package com.example.demo.async.config;

import static com.example.demo.async.constant.Constants.HEADER_X_CORRELATION_ID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpServletRequest) {
            ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(httpServletRequest);
            String correlationId = request.getHeader(HEADER_X_CORRELATION_ID);
            if (correlationId != null) {
                MDC.put(HEADER_X_CORRELATION_ID, correlationId);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(HEADER_X_CORRELATION_ID);
        }
    }
}
