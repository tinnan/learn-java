package com.example.jpademo.repo

import com.example.jpademo.JpaDemoApplication
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile
import spock.lang.Specification

@Slf4j
@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = JpaDemoApplication, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SpecialTypeRepositoryTest extends Specification {

    private static final String PG_USERNAME = "sa"
    private static final String PG_PASSWORD = "password"
    private static final String PG_DB_NAME = "postgres"

    static PostgreSQLContainer pgDbContainer = new PostgreSQLContainer<>("postgres:14.12-alpine")
            .withUsername(PG_USERNAME)
            .withPassword(PG_PASSWORD)
            .withDatabaseName(PG_DB_NAME)
            .withCopyFileToContainer(MountableFile.forClasspathResource("/docker/postgres/special-type/init-db.sql"),
                    "/docker-entrypoint-initdb.d/init-db.sql")

    @DynamicPropertySource
    static void setupProps(DynamicPropertyRegistry registry) {
        pgDbContainer.start()
        def pgUrl = pgDbContainer.getJdbcUrl()
        log.info("PG URL: {}", pgUrl)
        registry.add("spring.datasource.url", () -> pgUrl)
        registry.add("spring.datasource.username", () -> PG_USERNAME)
        registry.add("spring.datasource.password", () -> PG_PASSWORD)
    }

    @Autowired
    SpecialTypeRepository specialTypeRepository

    def "Should be able to query data from table with special type column and deserialize into target object"() {
        when:
        def data1 = specialTypeRepository.findById(1)

        then:
        data1.isPresent()
        verifyAll(data1.get()) {
            it.jsonColumn.header == "Header title"
            it.jsonColumn.description == ["Description 1", "Description 2"]
        }

        when:
        def data2 = specialTypeRepository.findById(2)

        then:
        data2.isPresent()
        verifyAll(data2.get()) {
            it.jsonColumn == null
        }
    }
}
