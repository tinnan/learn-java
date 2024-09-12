package com.example.demo.configprop.config;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Builder
@ToString
@Configuration
@ConfigurationProperties(prefix = "app")
@ConditionalOnProperty(name = "app.enable", havingValue = "true")
public class ConfigProperties {

    private Map<String, List<Config>> config;

    @Getter
    @Builder
    @ToString
    public static class Config {

        private String method;
        private String path;
        private String targetUrl;
    }
}
