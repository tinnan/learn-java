package com.example.demo.repository

import com.example.demo.domain.ActivityLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

@DataMongoTest
@Testcontainers
class ActivityLogRepositorySpec extends Specification {
    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    @Autowired(required = false)
    ActivityLogRepository activityLogRepository

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        mongoDBContainer.start()
        registry.add("spring.data.mongodb.uri", () ->   mongoDBContainer.replicaSetUrl)
    }

    def "when context is loaded, all expected bean are created"() {
        expect:
        activityLogRepository
    }

    def "Insert a document to activity_log collection"() {
        given: "Activity log data"
        ActivityLog activityLog = new ActivityLog()
        activityLog.setTxDatetime(LocalDateTime.now())
        activityLog.setStaffId("61643")
        activityLog.setBranchCode("001")
        activityLog.setChannel("Branch")
        activityLog.setRmidEc(8891481)
        activityLog.setIdType("CID")
        activityLog.setIdNo("1109299412120")
        activityLog.setServiceType("Create RM")
        activityLog.setActivityType("Dip Chip")
        activityLog.setActivityStatus("Failed")
        activityLog.setDetail(new ActivityLog.Detail("400", "Generic Server Error"))

        when: "Insert to collection"
        activityLogRepository.save(activityLog)

        then: "Should success"
        notThrown(Exception)

        when: "Try to query saved document with ID ${activityLog.id}"
        Optional<ActivityLog> savedActivityLog = activityLogRepository.findById(activityLog.id)

        then: "Saved document should exist"
        savedActivityLog.present
    }
}
