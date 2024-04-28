package com.example.demo

import com.example.demo.domain.ActivityLog
import com.example.demo.repository.ActivityLogRepository
import com.example.demo.service.ActivityLogService
import groovy.util.logging.Slf4j
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Slf4j
@EnableSharedInjection
abstract class ActivityLogSpecBase extends Specification {
    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
    // todo: investigate how to init DB container with script file.
//            .withCopyFileToContainer(MountableFile.forClasspathResource("/data/init-activity-log.js"),
//                    "/docker-entrypoint-initdb.d/*.js:ro")
    @Autowired(required = false)
    @Shared
    ActivityLogRepository activityLogRepository
    @Autowired(required = false)
    @Shared
    ActivityLogService activityLogService;

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        mongoDBContainer.start()
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.replicaSetUrl)
    }

    def setupSpec() {
        ActivityLog activityLog1 = new ActivityLog()
        activityLog1.id = 1
        activityLog1.txDatetime = LocalDateTime.parse("2024-04-15T03:41:33")
        activityLog1.staffId = "52134"
        activityLog1.branchCode = "001"
        activityLog1.channel = "Branch"
        activityLog1.rmidEc = 77318491
        activityLog1.idType = "CID"
        activityLog1.idNo = "1123900091841"
        activityLog1.serviceType = "Create RM"
        activityLog1.activityType = "Dip Chip"
        activityLog1.activityStatus = "Failed"
        activityLog1.detail = new ActivityLog.Detail("400", "Generic Server Error")
        ActivityLog activityLog2 = new ActivityLog();
        activityLog2.id = 2
        activityLog2.txDatetime = LocalDateTime.parse("2024-04-12T06:03:12")
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
        activityLog3.id = 3
        activityLog3.txDatetime = LocalDateTime.parse("2024-04-15T06:12:01")
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
        activityLog4.id = 4
        activityLog4.txDatetime = LocalDateTime.parse("2024-04-14T10:54:34")
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
}
