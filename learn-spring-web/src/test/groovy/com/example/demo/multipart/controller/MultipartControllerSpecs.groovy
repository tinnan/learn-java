package com.example.demo.multipart.controller


import com.example.demo.multipart.MultipartDemoApplication
import com.example.demo.multipart.model.MultipartFileUploadResponse
import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = MultipartDemoApplication)
class MultipartControllerSpecs extends Specification {
    private static final def PORT = TestSocketUtils.findAvailableTcpPort()
    private static final String API_UNDER_TEST_PATH = "/api/v1/multipart/file/upload"

    @DynamicPropertySource
    static def dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> PORT)
        registry.add("multipart-client.url", () -> "http://localhost:${PORT}")
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = PORT
    }

    def "Test request multipart form data"() {
        when:
        def response = RestAssured.given().contentType("multipart/form-data")
                .multiPart("fileName", "hello")
                .multiPart("file", "hello_file", "Hello world".getBytes(), "text/plain")
                .post(API_UNDER_TEST_PATH)

        then:
        response.statusCode() == 200
        verifyAll(response.body().as(MultipartFileUploadResponse)) {
            getUploadedFileName() == "hello_file"
            getUploadedContentType() == "text/plain"
            new String(getUploadedFileContent()) == "Hello world"
        }
    }
}
