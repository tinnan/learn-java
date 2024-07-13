package com.example.demo.testcontainer


import groovy.util.logging.Slf4j
import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Specification

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
class TestcontainerSetup extends Specification {

    protected static final String BASE_URI = "http://localhost"
    private static final String MONGO_USERNAME = "app_user"
    private static final String MONGO_PASSWORD = "app_password"
    private static final String MONGO_DB_NAME = "testdb"
    private static final String PG_USERNAME = "sa"
    private static final String PG_PASSWORD = "password"
    private static final String PG_DB_NAME = "testdb"

    // This will start single node MongoDB cluster.
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withEnv("MONGO_INITDB_USERNAME", MONGO_USERNAME)
            .withEnv("MONGO_INITDB_PASSWORD", MONGO_PASSWORD)
            .withEnv("MONGO_INITDB_DATABASE", MONGO_DB_NAME)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("/docker/mongo/init-db.sh"),
                    "/data/init/init-db.sh")

    static redisContainer = new GenericContainer("redis:6.2.6")
            .withExposedPorts(6379)

    static PostgreSQLContainer pgDbContainer = new PostgreSQLContainer<>("postgres:14.12-alpine")
            .withUsername(PG_USERNAME)
            .withPassword(PG_PASSWORD)
            .withDatabaseName(PG_DB_NAME)
            .withCopyFileToContainer(MountableFile.forClasspathResource("/docker/postgres/init-db.sql"),
                    "/docker-entrypoint-initdb.d/init-db.sql")

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

        redisContainer.start()
        registry.add("spring.data.redis.host", () -> redisContainer.host)
        registry.add("spring.data.redis.port", () -> redisContainer.firstMappedPort)

        pgDbContainer.start()
        def pgUrl = pgDbContainer.getJdbcUrl()
        log.info("PG URL: {}", pgUrl)
        registry.add("spring.datasource.url", () -> pgUrl)
        registry.add("spring.datasource.username", () -> PG_USERNAME)
        registry.add("spring.datasource.password", () -> PG_PASSWORD)
    }

    def "A"() {
        expect:
        1 == 1
    }
}
