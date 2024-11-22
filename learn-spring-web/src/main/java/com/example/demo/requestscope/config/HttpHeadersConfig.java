package com.example.demo.requestscope.config;

import com.example.demo.requestscope.config.context.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.annotation.RequestScope;

@Slf4j
@Configuration
public class HttpHeadersConfig {

    public static final String HTTP_HEADERS_BEAN_NAME = "httpHeaders";
    public static final String HTTP_HEADERS_REQUEST_SCOPE_ATTR = "scopedTarget." + HTTP_HEADERS_BEAN_NAME;

    @Primary
    @Bean(HTTP_HEADERS_BEAN_NAME)
    @RequestScope
    public HttpHeaders getHttpHeaders() {
        log.info("Creating HttpHeaders bean");
        return ContextUtils.getHttpHeaders();
    }
}
