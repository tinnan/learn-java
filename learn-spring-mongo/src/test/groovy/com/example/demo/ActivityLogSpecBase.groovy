package com.example.demo

import com.example.demo.domain.activitylog.ActivityLog
import com.example.demo.domain.activitylog.ActivityLogBase
import com.example.demo.repository.ActivityLogRepository
import com.example.demo.service.ActivityLogService
import com.mongodb.BasicDBObject
import com.mongodb.MongoCommandException
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import de.flapdoodle.embed.mongo.commands.MongodArguments
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.config.Storage
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.transitions.ImmutableMongod
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.embed.mongo.types.DistributionBaseUrl
import de.flapdoodle.reverse.TransitionWalker
import de.flapdoodle.reverse.transitions.Start
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.commons.lang3.StringUtils
import org.bson.Document
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Shared
import spock.lang.Specification

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.Instant
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = [EmbeddedMongoConfig])
@Slf4j
@EnableSharedInjection
abstract class ActivityLogSpecBase extends Specification {
    protected static final ID_1 = UUID.fromString("16F1F6CF-620C-448E-8276-D2DBB0A4A8FF")
    protected static final ID_2 = UUID.fromString("916A1D66-E338-4776-B151-D274600C6FAB")
    protected static final ID_3 = UUID.fromString("32F94973-50AE-43D0-BA85-AA4B08BD6608")
    protected static final ID_4 = UUID.fromString("A12F525E-E00D-4D82-B59A-538EE29B481E")
    private static final int MONGO_PORT = 60000
    private static final String DB_NAME = "testdb"
    private static final String DB_USERNAME = "testusr"
    private static final String DB_PASSWORD = "testpwd"
    private static final String DB_RS_NAME = "rs0"

    @Autowired(required = false)
    @Shared
    ActivityLogRepository activityLogRepository
    @Autowired(required = false)
    @Shared
    ActivityLogService activityLogService

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        def connectionString = "mongodb://localhost:${MONGO_PORT}/${DB_NAME}"

        registry.add("spring.data.mongodb.uri", () -> connectionString)
        registry.add("spring.data.mongodb.username", () -> DB_USERNAME)
        registry.add("spring.data.mongodb.password", () -> DB_PASSWORD)
        registry.add("spring.data.mongodb.dbname", () -> DB_NAME)
        registry.add("embedded-mongodb.enable", () -> "true")
        registry.add("embedded-mongodb.port", () -> MONGO_PORT)
        registry.add("embedded-mongodb.rs-name", () -> DB_RS_NAME)
    }

    def setupSpec() {
        ActivityLog activityLog1 = new ActivityLog()
        activityLog1.id = ID_1
        activityLog1.txDatetime = Instant.parse("2024-04-15T03:41:33Z")
        activityLog1.staffId = "52134"
        activityLog1.branchCode = "001"
        activityLog1.channel = "Branch"
        activityLog1.rmidEc = 77318491
        activityLog1.idType = "CID"
        activityLog1.idNo = "1123900091841"
        activityLog1.serviceType = "Create RM"
        activityLog1.activityType = "Dip Chip"
        activityLog1.activityStatus = "Failed"
        activityLog1.detail = new ActivityLogBase.Detail()
                .setErrorCode("400").setErrorMsg("Generic Server Error").setErrorFields("field_1")
        ActivityLog activityLog2 = new ActivityLog()
        activityLog2.id = ID_2
        activityLog2.txDatetime = Instant.parse("2024-04-12T06:03:12Z")
        activityLog2.staffId = "62007"
        activityLog2.branchCode = "002"
        activityLog2.channel = "Branch"
        activityLog2.rmidEc = 88714120
        activityLog2.idType = "PASSPORT"
        activityLog2.idNo = "AA99481250"
        activityLog2.serviceType = "Apply AL"
        activityLog2.activityType = "Phone No. Input"
        activityLog2.activityStatus = "Pass"
        ActivityLog activityLog3 = new ActivityLog();
        activityLog3.id = ID_3
        activityLog3.txDatetime = Instant.parse("2024-04-15T06:12:01Z")
        activityLog3.staffId = "62007"
        activityLog3.branchCode = "001"
        activityLog3.channel = "Branch"
        activityLog3.rmidEc = 11984812
        activityLog3.idType = "CID"
        activityLog3.idNo = "2881931000912"
        activityLog3.serviceType = "Apply INSUR"
        activityLog3.activityType = "Personal Info Input"
        activityLog3.activityStatus = "Pass"
        ActivityLog activityLog4 = new ActivityLog();
        activityLog4.id = ID_4
        activityLog4.txDatetime = Instant.parse("2024-04-14T10:54:34Z")
        activityLog4.staffId = "57219"
        activityLog4.branchCode = "003"
        activityLog4.channel = "Off Site"
        activityLog4.rmidEc = 77937114
        activityLog4.idType = "CID"
        activityLog4.idNo = "1094923812304"
        activityLog4.serviceType = "Create RM"
        activityLog4.activityType = "Address Info Input"
        activityLog4.activityStatus = "Pass"
        activityLogRepository.saveAll(List.of(activityLog1, activityLog2, activityLog3, activityLog4))
    }

    def "when context is loaded, all expected bean are created"() {
        expect:
        activityLogRepository
        activityLogService
    }

    @TestConfiguration
    @Slf4j
    static class EmbeddedMongoConfig {
        final int port
        final String rsName
        final String username
        final String password
        final String dbName
        final TransitionWalker.ReachedState<RunningMongodProcess> mongod

        EmbeddedMongoConfig(@Value('${embedded-mongodb.dist.url:}') String distBaseUrl, // Default=empty
                            @Value('${embedded-mongodb.port}') int port,
                            @Value('${embedded-mongodb.rs-name}') String rsName,
                            @Value('${spring.data.mongodb.username}') String username,
                            @Value('${spring.data.mongodb.password}') String password,
                            @Value('${spring.data.mongodb.dbname}') String dbName) {
            this.port = port
            this.rsName = rsName
            this.username = username
            this.password = password
            this.dbName = dbName

            ImmutableMongod.Builder builder = Mongod.builder()
                    .mongodArguments(Start.to(MongodArguments)
                            .initializedWith(MongodArguments.defaults()
                                    .withIsConfigServer(false)
                                    .withReplication(Storage.of(rsName, 1000))))
                    .net(Start.to(Net).initializedWith(Net.defaults().withPort(port)))
            if (StringUtils.isNotEmpty(distBaseUrl)) {
                log.info("Configure Mongo distribution download base URL to: {}", distBaseUrl)
                builder.distributionBaseUrl(Start.to(DistributionBaseUrl)
                        .initializedWith(DistributionBaseUrl.of(distBaseUrl)))
                // Disable SSL certificate validation when downloading MongoDB binaries from some private server.
                setTrustAll()
            }
            this.mongod = builder.build().start(Version.Main.V7_0)
        }

        private static void setTrustAll() {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        X509Certificate[] getAcceptedIssuers() {
                            return null
                        }

                        void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            }

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, new SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                boolean verify(String hostname, SSLSession session) {
                    return true
                }
            }

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
        }

        @PostConstruct
        void postConstruct() {
            def mongoAddress = this.mongod.current().getServerAddress()
            log.info("Embedded Mongo address: {}", mongoAddress.toString())

            try (MongoClient client = MongoClients.create("mongodb://${mongoAddress}")) {
                def adminDb = client.getDatabase("admin")

                BasicDBObject initRsCmd = new BasicDBObject("replSetInitiate", new Document())
                def initRsResult = adminDb.runCommand(initRsCmd)
                log.info("Init replica set result: {}", initRsResult)

                def replSetReady = adminDb.runCommand(new Document("replSetGetStatus", 1)).get("ok")
                log.info("Replica set ready: {}", replSetReady)

                int tryCount = 0
                while (true) {
                    BasicDBObject createUserCmd = new BasicDBObject("createUser", this.username)
                            .append("pwd", this.password)
                            .append("roles", List.of(new BasicDBObject("role", "readWrite").append("db", this.dbName)))
                    try {
                        Document createUserResult = adminDb.runCommand(createUserCmd)
                        log.info("Create user result: {}", createUserResult)
                        break
                    } catch (MongoCommandException e) {
                        // Mongo may throw NotWritablePrimary error during election period.
                        // Give it some time and try again.
                        tryCount += 1
                        if (tryCount >= 10) {
                            // Exceed try limit.
                            throw e
                        }
                        log.info("Primary node is not ready. Waiting for retry... (current try count: {})", tryCount)
                        TimeUnit.SECONDS.sleep(1)
                    }
                }
            }
        }

        @PreDestroy
        void preDestroy() {
            this.mongod.close()
        }
    }
}
