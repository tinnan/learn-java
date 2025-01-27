package com.example.demo.largeresponse

import com.example.demo.largeresponse.client.LargeResponseFeign
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = LargeResponseApplication)
class LargeResponseSpecs extends Specification {

    private static final String BASE_URI = "http://localhost"
    private static final int WM_PORT = TestSocketUtils.findAvailableTcpPort()

    @DynamicPropertySource
    def static dynamicProps(DynamicPropertyRegistry registry) {
        def wmUrl = "${BASE_URI}:${WM_PORT}"
        registry.add("feign.large-response.url", () -> wmUrl)
        RestAssured.baseURI = BASE_URI
        RestAssured.port = WM_PORT
    }

    @Shared
    WireMockServer wm = new WireMockServer(options().port(WM_PORT))
    @Autowired
    LargeResponseFeign largeResponseFeign

    def setupSpec() {
        wm.start()
    }

    def cleanup() {
        wm.resetAll()
    }

    def cleanupSpec() {
        wm.stop()
    }

    def "Should be able to handle large response data"() {
        given:
        wm.stubFor(get(urlPathEqualTo("/"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("large_response/large_response.json")))

        when:
        def response = largeResponseFeign.get()

        then:
        response.getItems().size() == 400000
    }
}
