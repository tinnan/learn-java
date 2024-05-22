package org.example.security.configuration;

import lombok.extern.slf4j.Slf4j;
import org.example.security.configuration.filter.JwtDecodeFilter;
import org.example.security.service.JwtDecodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain restApiFilterChain(HttpSecurity http,
        @Value("${security.filter.jwt.path.pattern}") String securePathMatchPattern) throws Exception {
        // Example matcher pattern: /**/secure/** = Match any path with subdirectory named "secure"
        return http.securityMatcher(new AntPathRequestMatcher(securePathMatchPattern))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterAfter(new JwtDecodeFilter(new JwtDecodeService()),
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
