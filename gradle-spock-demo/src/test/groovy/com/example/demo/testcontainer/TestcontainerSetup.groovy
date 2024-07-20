package com.example.demo.testcontainer

import com.azure.storage.blob.BlobServiceAsyncClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.example.demo.DemoApplication
import groovy.util.logging.Slf4j
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
@SpringBootTest(classes = [DemoApplication, AzuriteTestConfig],
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
class TestcontainerSetup extends Specification {

    protected static final String BASE_URI = "http://localhost"
    // Mongo constants - START
    private static final String MONGO_USERNAME = "app_user"
    private static final String MONGO_PASSWORD = "app_password"
    private static final String MONGO_DB_NAME = "testdb"
    // Mongo constants - END
    // Postgres constants - START
    private static final String PG_USERNAME = "sa"
    private static final String PG_PASSWORD = "password"
    private static final String PG_DB_NAME = "testdb"
    // Postgres constants - END
    // Azurite constants - START
    public static final String AZ_CLIENT_ID = "TEST_CLIENT_ID"
    public static final String AZ_TENANT_ID = "TEST_TENANT_ID"
    public static final String AZ_STORAGE_CONTAINER = "azfuncblobs"
    /*
        References for storage account name and account key.
        https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite?tabs=visual-studio%2Cblob-storage#well-known-storage-account-and-key
     */
    public static final String AZ_STORAGE_ACCOUNT = "devstoreaccount1"
    public static final String AZ_ACCOUNT_KEY = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw=="
    // Azurite constants - END

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

    /**
     * Certificate and key file is to enable SSL.
     */
    static GenericContainer azuriteContainer = new GenericContainer<>("mcr.microsoft.com/azure-storage/azurite")
            .withCopyFileToContainer(MountableFile.forClasspathResource("/docker/cert/127.0.0.1.pem"),
                    "/workspace/127.0.0.1.pem")
            .withCopyFileToContainer(MountableFile.forClasspathResource("/docker/cert/127.0.0.1-key.pem"),
                    "/workspace/127.0.0.1-key.pem")
            .withCommand("azurite", "--blobHost", "0.0.0.0", "--queueHost", "0.0.0.0", "--tableHost", "0.0.0.0",
                    "--cert", "/workspace/127.0.0.1.pem", "--key", "/workspace/127.0.0.1-key.pem")
            .withExposedPorts(10000, 10001, 10002)

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

        azuriteContainer.start()
        def azuriteBlobServicePort = azuriteContainer.getMappedPort(10000)
        def azuriteQueueServicePort = azuriteContainer.getMappedPort(10001)
        def azuriteTableServicePort = azuriteContainer.getMappedPort(10002)
        log.info("Azurite blob service port: {}", azuriteBlobServicePort)
        log.info("Azurite queue service port: {}", azuriteQueueServicePort)
        log.info("Azurite table service port: {}", azuriteTableServicePort)

        registry.add("spring.cloud.azure.storage.blob.endpoint", () -> "https://127.0.0.1:${azuriteBlobServicePort}/${AZ_STORAGE_ACCOUNT}/${AZ_STORAGE_CONTAINER}")
        registry.add("spring.cloud.azure.storage.blob.container-name", () -> AZ_STORAGE_CONTAINER)
        registry.add("spring.cloud.azure.storage.blob.client-id", () -> AZ_CLIENT_ID)
        registry.add("spring.cloud.azure.storage.blob.tenant-id", () -> AZ_TENANT_ID)
    }

    @TestConfiguration
    class AzuriteTestConfig {

        @Bean
        BlobServiceClient blobServiceClient(@Value('${spring.cloud.azure.storage.blob.endpoint}') String blobEndpoint) {

            String[] connectionStringComponents = [
                    "DefaultEndpointsProtocol=https",
                    "AccountName=" + AZ_STORAGE_ACCOUNT,
                    "AccountKey=" + AZ_ACCOUNT_KEY,
                    "BlobEndpoint=" + blobEndpoint
            ]
            return new BlobServiceClientBuilder().connectionString(String.join(";", connectionStringComponents)).buildClient()
        }

        @Bean
        BlobServiceAsyncClient blobServiceAsyncClient(@Value('${spring.cloud.azure.storage.blob.endpoint}') String blobEndpoint) {

            String[] connectionStringComponents = [
                    "DefaultEndpointsProtocol=https",
                    "AccountName=" + AZ_STORAGE_ACCOUNT,
                    "AccountKey=" + AZ_ACCOUNT_KEY,
                    "BlobEndpoint=" + blobEndpoint
            ]
            return new BlobServiceClientBuilder().connectionString(String.join(";", connectionStringComponents)).buildAsyncClient()
        }
    }

    def "A"() {
        expect:
        1 == 1
    }
}
