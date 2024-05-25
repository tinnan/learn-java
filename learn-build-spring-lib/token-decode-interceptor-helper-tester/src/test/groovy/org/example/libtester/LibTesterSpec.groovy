package org.example.libtester

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
      "permissions": ["INQUIRE", "GENERATE"]
    }
     */
    private static final String JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdGFmZl9pZCI6IjYxNjQzIiwicGVybWlzc2lvbnMiOlsiSU5RVUlSRSIsIkdFTkVSQVRFIl19.4nS0XLmfRQdAGXGafWsD3YrdGkUGBGjqGlw3r_XyiEs"

    def "Test access endpoints"() {
        when:
        def response = RestAssured.given()
                .headers(createHeaders(token))
                .get(url)

        then:
        response.statusCode() == expectedStatus
        response.body.asString() == expectedBody

        where:
        token | url                                   | expectedStatus | expectedBody
        JWT   | "/api/v1/secure/interceptor/inquire"  | 200            | "Hello inquire."
        JWT   | "/api/v1/secure/interceptor/generate" | 200            | "Hello generate."
        JWT   | "/api/v1/secure/interceptor/count"    | 403            | "Requires permission COUNT to perform the action."
        JWT   | "/api/v1/secure/interceptor/sum"      | 200            | "50"
        ""    | "/api/v1/secure/interceptor/inquire"  | 403            | "Requires permission INQUIRE to perform the action."
        ""    | "/api/v1/secure/interceptor/generate" | 403            | "Requires permission GENERATE to perform the action."
        ""    | "/api/v1/secure/interceptor/count"    | 403            | "Requires permission COUNT to perform the action."
        ""    | "/api/v1/secure/interceptor/sum"      | 200            | "50"
    }

    def createHeaders(String token) {
        return [
                "Authorization": "Bearer ${token}"
        ]
    }
}
