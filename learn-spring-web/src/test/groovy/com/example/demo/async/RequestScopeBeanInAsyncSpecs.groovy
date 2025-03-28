package com.example.demo.async

import com.example.demo.async.constant.Constants
import com.example.demo.async.service.RequestScopeBeanInAsyncTestService
import groovy.util.logging.Slf4j
import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.util.TestSocketUtils
import org.springframework.util.StopWatch
import spock.lang.Specification

@Slf4j
@TestPropertySource(properties = [
        "com.example.demo.async.concurrency-mode=ASYNC"
])
@ActiveProfiles("async")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = AsyncDemoApplication)
class RequestScopeBeanInAsyncSpecs extends Specification {

    static def port = TestSocketUtils.findAvailableTcpPort()
    StopWatch stopWatch

    @DynamicPropertySource
    static def dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> port)

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    def setup() {
        stopWatch = new StopWatch()
        stopWatch.start()
    }

    def cleanup() {
        RequestScopeBeanInAsyncTestService.resetStorage()
        stopWatch.stop()
        log.info("Test elapsed: {} ms", stopWatch.getTotalTimeMillis())
    }

    def "Async process should executes successfully when call to API that waits for async to complete"() {
        when:
        def response = RestAssured.given()
                .header(Constants.HEADER_X_CORRELATION_ID, "x-cor-wait")
                .post("/api/v1/async/request-scope-bean/wait")

        then:
        response.statusCode() == 200
        RequestScopeBeanInAsyncTestService.getStorageValue() == "x-cor-wait"
    }

    def "Async process should executes successfully when call to API that does not wait for async to complete"() {
        when:
        def response = RestAssured.given()
                .header(Constants.HEADER_X_CORRELATION_ID, "x-cor-no-wait")
                .post("/api/v1/async/request-scope-bean/no-wait")

        then:
        response.statusCode() == 200

        and:
        RequestScopeBeanInAsyncTestService.waitForSetStorageValue(2000)

        then:
        RequestScopeBeanInAsyncTestService.getStorageValue() == "x-cor-no-wait"
    }
}
