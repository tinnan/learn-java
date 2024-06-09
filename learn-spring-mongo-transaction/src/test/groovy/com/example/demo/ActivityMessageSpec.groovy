package com.example.demo

import com.example.demo.domain.ActivityLog
import com.example.demo.domain.ActivityMessage
import com.example.demo.repository.ActivityLogRepository
import com.example.demo.repository.ActivityMessageRepository
import com.example.demo.service.ActivityMessageService
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = DemoApplication)
@Testcontainers
@Slf4j
@EnableSharedInjection
class ActivityMessageSpec extends Specification {
    private static final String MESSAGE_ID = "705e3657-a9b3-49cd-b011-23fca732db01"
    private static final String ACTIVITY_LOG_ID = "4b5131a3-dbc4-4d7f-b7a9-81fe0f064919"
    private static final String DB_NAME = "admin_panel"
    private static final String DB_USERNAME = "app_user"
    private static final String DB_PASSWORD = "app_password"

    @Shared
    @Autowired
    ActivityMessageService activityMessageService
    @Shared
    @Autowired
    ActivityMessageRepository activityMessageRepository
    @Shared
    @Autowired
    ActivityLogRepository activityLogRepository
    @Shared
    @Autowired
    ObjectMapper objectMapper

    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withEnv("MONGO_INITDB_USERNAME", DB_USERNAME)
            .withEnv("MONGO_INITDB_PASSWORD", DB_PASSWORD)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("/data/init-db.sh"),
                    "/data/init/init-db.sh")

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        mongoDBContainer.start()
        def execResult = mongoDBContainer
                .execInContainer("sh", "/data/init/init-db.sh")
        log.info("Init user result: {}", execResult.toString())

        def databaseUrl = mongoDBContainer.getReplicaSetUrl(DB_NAME)
        log.info("Database URL: {}", databaseUrl)

        registry.add("spring.data.mongodb.uri", () -> databaseUrl)
        registry.add("spring.data.mongodb.username", () -> DB_USERNAME)
        registry.add("spring.data.mongodb.password", () -> DB_PASSWORD)
        registry.add("spring.data.mongodb.dbname", () -> DB_NAME)
    }

    def setup() {
        ActivityLog activityLog = new ActivityLog()
        activityLog.setId(ACTIVITY_LOG_ID)
        activityLog.txDatetime = Instant.parse("2024-04-15T03:41:33Z")
        activityLog.staffId = "52134"
        activityLog.branchCode = "001"
        activityLog.channel = "Branch"
        activityLog.rmidEc = 77318491
        activityLog.idType = "CID"
        activityLog.idNo = "1123900091841"
        activityLog.serviceType = "Create RM"
        activityLog.activityType = "Dip Chip"
        activityLog.activityStatus = "Failed"
        activityLog.detail = new ActivityLog.Detail()
                .setErrorCode("400").setErrorMsg("Generic Server Error").setErrorFields("field_1")

        ActivityMessage activityMessage = ActivityMessage.builder().id(MESSAGE_ID)
                .data(objectMapper.writeValueAsString(activityLog))
                .build()

        activityMessageRepository.save(activityMessage)
    }

    def cleanup() {
        activityMessageRepository.deleteAll()
        activityLogRepository.deleteAll()
    }

    def "When saveMessageToActivityLog success then ActivityLog is saved and process status is updated to 'SUCCESS'"() {
        when:
        activityMessageService.saveMessageToActivityLog(MESSAGE_ID, false)

        then:
        def activityLog = activityLogRepository.findById(ACTIVITY_LOG_ID)
        activityLog.isPresent()
        activityLog.get().id == ACTIVITY_LOG_ID

        and:
        def activityMessage = activityMessageRepository.findById(MESSAGE_ID)
        activityMessage.isPresent()
        activityMessage.get().processStatus == "SUCCESS"
    }

    def "When saveMessageToActivityLog error occurs after save&update then ActivityLog is not saved and process status not updated"() {
        when:
        activityMessageService.saveMessageToActivityLog(MESSAGE_ID, true)

        then:
        thrown(IllegalStateException)

        and:
        def activityLog = activityLogRepository.findById(ACTIVITY_LOG_ID)
        activityLog.isEmpty()

        and:
        def activityMessage = activityMessageRepository.findById(MESSAGE_ID)
        activityMessage.isPresent()
        activityMessage.get().processStatus == null
    }
}
