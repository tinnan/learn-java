package com.example.demo.testcontainer

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import groovy.util.logging.Slf4j
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Specification

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
class MongoTestContainer extends Specification {
    protected static final String BASE_URI = "http://localhost"
    private static final String MONGO_USERNAME = "app_user"
    private static final String MONGO_PASSWORD = "app_password"
    private static final String MONGO_DB_NAME = "testdb"

    // This will start single node MongoDB cluster.
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withEnv("MONGO_INITDB_USERNAME", MONGO_USERNAME)
            .withEnv("MONGO_INITDB_PASSWORD", MONGO_PASSWORD)
            .withEnv("MONGO_INITDB_DATABASE", MONGO_DB_NAME)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("/docker/mongo/init-db.sh"),
                    "/data/init/init-db.sh")

    @DynamicPropertySource
    static void setupProps(DynamicPropertyRegistry registry) {
        def port = TestSocketUtils.findAvailableTcpPort()
        RestAssured.baseURI = BASE_URI
        RestAssured.port = port
        registry.add("server.port", () -> port)

        mongoDBContainer.start()
        def initDbResult = mongoDBContainer
                .execInContainer("sh", "/data/init/init-db.sh")
        log.info("Init DB result: {}", initDbResult.toString())

        def databaseUrl = mongoDBContainer.getReplicaSetUrl(MONGO_DB_NAME)
        log.info("Database URL: {}", databaseUrl)

        registry.add("spring.data.mongodb.uri", () -> databaseUrl)
        registry.add("spring.data.mongodb.username", () -> MONGO_USERNAME)
        registry.add("spring.data.mongodb.password", () -> MONGO_PASSWORD)
        registry.add("spring.data.mongodb.dbname", () -> MONGO_DB_NAME)
    }

    @Autowired
    MongoTemplate mongoTemplate

    def "Verify MongoDB connection"() {
        given:
        DBObject ping = new BasicDBObject("ping", "1");

        when:
        def answer = mongoTemplate.getDb().runCommand(ping)
        log.info("Answer: {}", answer)

        then:
        notThrown(Exception)
        answer.get("ok") == 1
    }
}
