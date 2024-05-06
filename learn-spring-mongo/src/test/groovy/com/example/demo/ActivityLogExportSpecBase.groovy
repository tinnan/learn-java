package com.example.demo

import com.example.demo.service.ActivityLogService
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.output.WaitingConsumer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Slf4j
@EnableSharedInjection
abstract class ActivityLogExportSpecBase extends Specification {
    @Value('${activity-log.export.path}')
    @Shared
    String exportFilePath
    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withCopyFileToContainer(MountableFile.forClasspathResource("./data/init-activity-log.js"),
                    "/docker-entrypoint-initdb.d/init-activity-log.js")
    // todo: why waoting strategy does not work?
//            .waitingFor(Wait.forLogMessage(".*MongoDB init process complete.*", 1))
    @Autowired(required = false)
    @Shared
    ActivityLogService activityLogService

    static {
        long start = System.currentTimeMillis()
        mongoDBContainer.start()
        // Workaround for Log message waiting strategy that does not work.
        // Use followOutput with WaitingConsumer to wait for expected string to appear in container log
        // before proceeding.
        WaitingConsumer consumer = new WaitingConsumer();
        mongoDBContainer.followOutput(consumer, OutputFrame.OutputType.STDOUT)
        consumer.waitUntil(frame -> frame.getUtf8String().contains("MongoDB init process complete"), 60, TimeUnit.SECONDS)
        long end = System.currentTimeMillis()
        log.info("MongoDB container initialization process took: {} ms", end - start)
//        def containerLogs = mongoDBContainer.logs
//        log.info(containerLogs)
    }

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        def mongoUrl = mongoDBContainer.replicaSetUrl
        log.info("Mongo URL: {}", mongoUrl)
        registry.add("spring.data.mongodb.uri", () -> mongoUrl)
    }

    def setupSpec() {
        def execResult = mongoDBContainer.execInContainer(
                "mongosh", "--quiet",
                "--eval", "'use test'",
                "--eval", "'show collections'",
                "mongodb://localhost/")
        log.info("Collections: {}", execResult.getStdout())
        execResult = mongoDBContainer.execInContainer(
                "mongosh", "--quiet",
                "--eval", "'use test'",
                "--eval", "'db.activity_log.countDocuments()'",
                "mongodb://localhost/")
        log.info("Activity log count: {}", execResult.getStdout())
        execResult = mongoDBContainer.execInContainer(
                "mongosh", "--quiet",
                "--eval", "'use test'",
                "--eval", "'db.activity_log.findOne()'",
                "mongodb://localhost/")
        log.info("First activity log: {}", execResult.getStdout())
    }

    def cleanup() {
        FileUtils.deleteQuietly(new File(exportFilePath))
    }

    def "when context loaded, all expected beans are created"() {
        expect:
        activityLogService
    }
}
