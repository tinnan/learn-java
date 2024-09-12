package com.example.demo.feignconfig.config;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okTextXml;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockServerConfig {

    @Bean(name = "server1")
    public WireMockServer server1(@Value("${mock-server-1.port}") int port) {
        WireMockServer srv = new WireMockServer(options().port(port));
        srv.stubFor(get(urlPathEqualTo("/server"))
            .willReturn(okTextXml("Get from server 1")));
        srv.stubFor(post(urlPathEqualTo("/server"))
            .willReturn(okTextXml("Post from server 1")));
        return srv;
    }

    @Bean(name = "server2")
    public WireMockServer server2(@Value("${mock-server-2.port}") int port) {
        WireMockServer srv = new WireMockServer(options().port(port));
        srv.stubFor(get(urlPathEqualTo("/server"))
            .willReturn(okTextXml("Get from server 2")));
        srv.stubFor(post(urlPathEqualTo("/server"))
            .willReturn(okTextXml("Post from server 2")));
        return srv;
    }
}
