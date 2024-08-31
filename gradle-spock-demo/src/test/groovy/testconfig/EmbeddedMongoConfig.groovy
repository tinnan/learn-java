package testconfig

import testutils.FileServerConstants
import testutils.SslUtils
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
import de.flapdoodle.embed.process.config.TimeoutConfig
import de.flapdoodle.embed.process.net.DownloadToPath
import de.flapdoodle.embed.process.net.UrlStreams
import de.flapdoodle.embed.process.transitions.DownloadPackage
import de.flapdoodle.net.URLConnections
import de.flapdoodle.reverse.TransitionWalker
import de.flapdoodle.reverse.transitions.Start
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.commons.lang3.StringUtils
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.test.context.TestConfiguration

import java.nio.file.Path
import java.util.concurrent.TimeUnit

/*
    Use this config with @SpringBootTest(classes = {EmbeddedMongoConfig.class})
 */
@TestConfiguration
@ConditionalOnProperty(name = "embedded-mongodb.enable", havingValue = "true")
@Slf4j
class EmbeddedMongoConfig {
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
        // Default download location of the library is https://fastdl.mongodb.org/mongo
        if (StringUtils.isNotEmpty(distBaseUrl)) {
            log.info("Configure Mongo distribution download base URL to: {}", distBaseUrl)
            // By default, the library supports basic authentication for the download URL by
            // specifying username and password in the URL like this
            // https://<username>:<password>@file.server.com/mongo which will be displayed in log message.
            // To hide the sensitive information, write custom DownloadToPath.
            DownloadToPath customDownload = new DownloadToPath() {
                @Override
                void download(URL url, Path destination, Optional<Proxy> proxy, String userAgent, TimeoutConfig timeoutConfig, DownloadToPath.DownloadCopyListener copyListener) throws IOException {
                    URLConnection connection = URLConnections.urlConnectionOf(url)
                    String basicAuth = FileServerConstants.FILE_SERVICE_USER + ":" + FileServerConstants.FILE_SERVER_PASSWORD
                    connection.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder().encode(basicAuth.getBytes())))

                    connection.setRequestProperty("User-Agent", userAgent)
                    connection.setConnectTimeout(timeoutConfig.getConnectionTimeout())
                    connection.setReadTimeout(timeoutConfig.getReadTimeout())
                    UrlStreams.downloadTo(connection, destination, copyListener)
                }
            }
            builder.distributionBaseUrl(Start.to(DistributionBaseUrl).initializedWith(DistributionBaseUrl.of(distBaseUrl)))
                    .downloadPackage(DownloadPackage.withDefaults().withDownloadToPath(customDownload))
            // Disable SSL certificate validation.
            SslUtils.setTrustAll()
        }
        this.mongod = builder.build().start(Version.Main.V7_0)
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