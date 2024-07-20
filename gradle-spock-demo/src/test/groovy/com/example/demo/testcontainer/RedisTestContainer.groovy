package com.example.demo.testcontainer


import groovy.util.logging.Slf4j
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
class RedisTestContainer extends Specification {
    protected static final String BASE_URI = "http://localhost"

    static redisContainer = new GenericContainer("redis:6.2.6")
            .withExposedPorts(6379)

    @DynamicPropertySource
    static void setupProps(DynamicPropertyRegistry registry) {
        def port = TestSocketUtils.findAvailableTcpPort()
        RestAssured.baseURI = BASE_URI
        RestAssured.port = port
        registry.add("server.port", () -> port)

        redisContainer.start()
        registry.add("spring.data.redis.host", () -> redisContainer.host)
        registry.add("spring.data.redis.port", () -> redisContainer.firstMappedPort)
    }

    @Autowired
    RedisTemplate<String, String> redisTemplate

    def "Verify Redis connection"() {
        given:
        redisTemplate.opsForValue().set("K1", "V1")

        when:
        def result = redisTemplate.opsForValue().get("K1")

        then:
        result == "V1"
    }
}
