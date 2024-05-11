package com.example.demo.controller

import com.example.demo.clients.FraudClient
import com.example.demo.controller.advisor.ExceptionHandlers
import io.restassured.RestAssured
import io.restassured.config.LogConfig
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import org.spockframework.spring.SpringBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static io.restassured.http.ContentType.JSON
import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CustomerControllerTest extends Specification {
    private static final String BASE_URI = "http://localhost"
    private static final int PORT = findAvailableTcpPort()
    private static final String ELIGIBLE_CUSTOMER_EMAIL = "john.d@gmail.com"
    private static final String FRAUDSTER_CUSTOMER_EMAIL = "jane.d@gmail.com"

    @DynamicPropertySource
    static void dynamicProps(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> PORT)
        registry.add("feign.client.customer.url", () -> "${BASE_URI}:${PORT}")
        registry.add("feign.client.fraud.url", () -> "${BASE_URI}:${PORT}")
        RestAssured.port = PORT
    }

    def setupSpec() {
        RestAssured.baseURI = BASE_URI
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)
                        .enablePrettyPrinting(true))
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
    }

    @SpringBean
    FraudClient fraudClient = Stub() {
        isFraudster(ELIGIBLE_CUSTOMER_EMAIL) >> false
        isFraudster(FRAUDSTER_CUSTOMER_EMAIL) >> true
    }

    def "should success when registering eligible customer"() {
        given:
        Map request = [username: "johnd",
                       email   : ELIGIBLE_CUSTOMER_EMAIL]

        when:
        def response = RestAssured.with()
                .contentType(JSON).body(toJson(request))
                .when().post("/api/v1/customer")

        then:
        response.statusCode() == HttpStatus.OK.value()
    }

    def "should fail when registering ineligible customer"() {
        given:
        Map request = [username: "janed",
                       email   : FRAUDSTER_CUSTOMER_EMAIL]

        when:
        def response = RestAssured.with()
                .contentType(JSON).body(toJson(request))
                .when().post("/api/v1/customer")

        then:
        response.statusCode() == ExceptionHandlers.HTTP_STATUS_INELIGIBLE_CUSTOMER
    }
}
