package com.example.demo.async


import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = AsyncDemoApplication)
class AsyncApiCallSpecs extends Specification {
    static def port = 58080
    static def customerServiceDelayMs = 2000
    static def fraudServiceDelayMs = 3000

    @DynamicPropertySource
    static def dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> port)
        registry.add("clients.customer.url", () -> "http://localhost:${port}")
        registry.add("clients.fraud.url", () -> "http://localhost:${port}")
        registry.add("service.customer.response.delay-ms", () -> customerServiceDelayMs)
        registry.add("service.fraud.response.delay-ms", () -> fraudServiceDelayMs)
        registry.add("logging.level.com.example.demo.async.clients", () -> "debug")
        registry.add("spring.cloud.openfeign.client.config.default.loggerLevel", () -> "full")
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    def "Calling orchestration service should not take longer than expected duration"() {
        given:
        def request = [
                customerEmail: "john.d@test.com",
                productId    : "SCP-173"
        ]
        def start = System.currentTimeMillis()

        when:
        def response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post("/api/v1/product/apply")

        and:
        def end = System.currentTimeMillis()

        then:
        response.statusCode() == HttpStatus.OK.value()

        and:
        final overheadMs = 500
        // todo: is there a better way to measure elapsed time of the API call. (Use Spy?)
        end - start < Math.max(customerServiceDelayMs, fraudServiceDelayMs) + overheadMs
    }
}
