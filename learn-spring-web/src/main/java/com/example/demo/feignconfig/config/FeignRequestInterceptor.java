package com.example.demo.feignconfig.config;

import feign.Request.HttpMethod;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignRequestInterceptor implements RequestInterceptor {

    @Value("${mock-server.url}")
    private final String mockServerUrl;

    @Override
    public void apply(RequestTemplate template) {
        log.info("Request url: {}", template.url());
        log.info("Request path: {}", template.path());
        if (HttpMethod.POST.name().equals(template.method()) && template.path().equals("/server")) {
            template.target(mockServerUrl);
        }
    }
}
