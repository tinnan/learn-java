package com.example.demo.async


import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = AsyncDemoApplication)
@AutoConfigureMockMvc
@EnableSharedInjection
class AsyncApiCallSpecs extends Specification {
    static def port = 58080
    static def customerServiceDelayMs = 2000
    static def fraudServiceDelayMs = 3000
    @Autowired
    @Shared
    MockMvc mvc
    @Autowired
    @Shared
    ObjectMapper objectMapper

    @DynamicPropertySource
    static def dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> port)
        registry.add("clients.customer.url", () -> "http://localhost:${port}")
        registry.add("clients.fraud.url", () -> "http://localhost:${port}")
        registry.add("service.customer.response.delay-ms", () -> customerServiceDelayMs)
        registry.add("service.fraud.response.delay-ms", () -> fraudServiceDelayMs)
    }

    def "Calling orchestration service should not take longer than expected duration"() {
        given:
        Map request = [
                customerId: 1,
                productId: "SCP-173"
        ]
        def start = System.currentTimeMillis()

        when:
        def response = mvc.perform(post("/api/v1/product/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().response

        and:
        def end = System.currentTimeMillis()

        then:
        response.status == HttpStatus.OK.value()

        and:
        // todo: is there a better way to measure elapsed time of the API call. (Use Spy?)
        end - start < Math.max(customerServiceDelayMs, fraudServiceDelayMs) + 500
    }
}
