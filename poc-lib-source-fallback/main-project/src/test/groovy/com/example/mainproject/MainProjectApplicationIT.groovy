package com.example.mainproject

import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MainProjectApplicationIT extends Specification {

    private static final String BASE_URI = "http://localhost"
    private static final int PORT = TestSocketUtils.findAvailableTcpPort()

    @DynamicPropertySource
    static def setupProps(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> PORT)
        RestAssured.baseURI = BASE_URI
        RestAssured.port = PORT
    }

    def "Test app"() {
        when:
        def response = RestAssured.given().get("/")

        then:
        response.statusCode() == 200
        response.body().asString() == "(LOCAL) Hello James, Moa"
    }
}
