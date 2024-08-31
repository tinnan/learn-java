package integration.testcontainer

import com.example.demo.DemoApplication
import groovy.util.logging.Slf4j
import io.restassured.RestAssured
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Specification

@Testcontainers
@SpringBootTest(
        classes = [DemoApplication],
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
class PostgresqlTestContainer extends Specification {
    protected static final String BASE_URI = "http://localhost"
    private static final String PG_USERNAME = "sa"
    private static final String PG_PASSWORD = "password"
    private static final String PG_DB_NAME = "testdb"

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

        pgDbContainer.start()
        def pgUrl = pgDbContainer.getJdbcUrl()
        log.info("PG URL: {}", pgUrl)
        registry.add("spring.datasource.url", () -> pgUrl)
        registry.add("spring.datasource.username", () -> PG_USERNAME)
        registry.add("spring.datasource.password", () -> PG_PASSWORD)
    }

    @Autowired
    EntityManager entityManager

    def "Verify Postgresql connection"() {
        given:
        String query = "select 'hello'"

        when:
        def result = entityManager.createNativeQuery(query).getSingleResult()
        log.info("Query result: {}", result)

        then:
        result == "hello"
    }
}
