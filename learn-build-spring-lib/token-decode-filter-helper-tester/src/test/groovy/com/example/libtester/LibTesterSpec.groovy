package com.example.libtester

import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import spock.lang.Specification

@SpringBootTest(classes = LibTesterApplication, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class LibTesterSpec extends Specification {
    private static final String BASE_URI = "http://localhost"
    private static final int PORT = TestSocketUtils.findAvailableTcpPort()

    @DynamicPropertySource
    static def dynamicProps(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> PORT)
    }

    def setupSpec() {
        RestAssured.baseURI = BASE_URI
        RestAssured.port = PORT
    }

    /*
    Token with the following payload:
    {
      "staff_id": "61643",
      "permissions": ["INQUIRE", "ROLE_GENERATE"]
    }
     */
    private static final String JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdGFmZl9pZCI6IjYxNjQzIiwicGVybWlzc2lvbnMiOlsiSU5RVUlSRSIsIlJPTEVfR0VORVJBVEUiXX0.HqQ-NGw3b6DZ8FhMoLwvblRNgziUdLe6CAKgzTqjqFI"

    def "Test access endpoints"() {
        when:
        def response = RestAssured.given()
                .headers(createHeaders(token))
                .get(url)

        then:
        response.statusCode() == expectedStatus

        where:
        token | url                   | expectedStatus
        JWT   | "/api/v1/secure/filter/inquire"  | 200
        JWT   | "/api/v1/secure/filter/generate" | 200
        JWT   | "/api/v1/secure/filter/count"    | 403
        JWT   | "/api/v1/secure/filter/sum"      | 200
        ""    | "/api/v1/secure/filter/inquire"  | 403
        ""    | "/api/v1/secure/filter/generate" | 403
        ""    | "/api/v1/secure/filter/count"    | 403
        ""    | "/api/v1/secure/filter/sum"      | 200
    }

    def createHeaders(String token) {
        return [
                "Authorization": "Bearer ${token}"
        ]
    }
}
