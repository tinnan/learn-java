package com.example.demo.requestscope.config;

import static com.example.demo.requestscope.config.context.ContextUtils.createHttpHeaders;

import com.example.demo.requestscope.config.context.CustomRequestScopeAttr;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class HttpHeadersConfig {

    public static final String HTTP_HEADERS_BEAN_NAME = "httpHeaders";
    public static final String HTTP_HEADERS_REQUEST_SCOPE_ATTR = "scopedTarget." + HTTP_HEADERS_BEAN_NAME;

    @Primary
    @Bean(HTTP_HEADERS_BEAN_NAME)
    @RequestScope
    public HttpHeaders getHttpHeaders() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes attributes) {
            return createHttpHeaders(attributes);
        } else if (requestAttributes instanceof CustomRequestScopeAttr attributes) {
            HttpHeaders httpHeaders = (HttpHeaders) attributes.getAttribute(HTTP_HEADERS_REQUEST_SCOPE_ATTR,
                RequestAttributes.SCOPE_REQUEST);
            if (Objects.isNull(httpHeaders)) {
                throw new IllegalStateException(String.format("Not found request scope: %s", HTTP_HEADERS_BEAN_NAME));
            }
            return httpHeaders;
        }
        return new HttpHeaders();
    }
}
