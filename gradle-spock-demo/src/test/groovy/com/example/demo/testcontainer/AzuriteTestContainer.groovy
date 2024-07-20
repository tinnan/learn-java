package com.example.demo.testcontainer

import com.azure.core.http.rest.Response
import com.azure.storage.blob.*
import com.azure.storage.blob.models.BlockBlobItem
import com.azure.storage.blob.options.BlobParallelUploadOptions
import groovy.util.logging.Slf4j
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Specification

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
class AzuriteTestContainer extends Specification {
    protected static final String BASE_URI = "http://localhost"

    public static final String AZ_CLIENT_ID = "TEST_CLIENT_ID"
    public static final String AZ_TENANT_ID = "TEST_TENANT_ID"
    public static final String AZ_STORAGE_CONTAINER = "azfuncblobs"
    /*
        References for storage account name and account key.
        https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite?tabs=visual-studio%2Cblob-storage#well-known-storage-account-and-key
     */
    public static final String AZ_STORAGE_ACCOUNT = "devstoreaccount1"
    public static final String AZ_ACCOUNT_KEY = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw=="

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
    static class AzuriteTestConfig {

        @Bean
        BlobServiceClient blobServiceClient(
                @Value('${spring.cloud.azure.storage.blob.endpoint}') String blobEndpoint,
                @Value('${spring.cloud.azure.storage.blob.container-name}') String azureStorageContainerName) {

            String[] connectionStringComponents = [
                    "DefaultEndpointsProtocol=https",
                    "AccountName=" + AZ_STORAGE_ACCOUNT,
                    "AccountKey=" + AZ_ACCOUNT_KEY,
                    "BlobEndpoint=" + blobEndpoint
            ]
            String connectionString = String.join(";", connectionStringComponents)
            log.info("Init BlobServiceClient with connection string: {}", connectionString)
            return new BlobServiceClientBuilder().connectionString(connectionString).buildClient()
        }

        @Bean
        BlobServiceAsyncClient blobServiceAsyncClient(@Value('${spring.cloud.azure.storage.blob.endpoint}') String blobEndpoint) {

            String[] connectionStringComponents = [
                    "DefaultEndpointsProtocol=https",
                    "AccountName=" + AZ_STORAGE_ACCOUNT,
                    "AccountKey=" + AZ_ACCOUNT_KEY,
                    "BlobEndpoint=" + blobEndpoint
            ]
            String connectionString = String.join(";", connectionStringComponents)
            log.info("Init BlobServiceAsyncClient with connection string: {}", connectionString)
            return new BlobServiceClientBuilder().connectionString(connectionString).buildAsyncClient()
        }
    }

    @Value('${spring.cloud.azure.storage.blob.container-name}')
    String azureStorageContainerName
    @Autowired
    BlobServiceClient blobServiceClient
    @Autowired
    BlobServiceAsyncClient blobServiceAsyncClient

    def "Verify Azure blob storage connection"() {
        given:
        String filePath = "document/file_1.txt"
        String data = "hello world"
        ByteArrayInputStream dataStream = new ByteArrayInputStream(data.getBytes())

        and:
        BlobContainerClient blobContainerClient = this.blobServiceClient
                .getBlobContainerClient(this.azureStorageContainerName)
        blobContainerClient.createIfNotExists()

        when:
        log.info("Uploading file.")
        BlobContainerAsyncClient blobContainerAsyncClient = this.blobServiceAsyncClient
                .getBlobContainerAsyncClient(this.azureStorageContainerName)
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(filePath)
        BlobParallelUploadOptions options = new BlobParallelUploadOptions(dataStream)
        Response<BlockBlobItem> response = (Response) blobAsyncClient
                .uploadWithResponse(options).block()

        then:
        response.getStatusCode() == 201
    }
}
