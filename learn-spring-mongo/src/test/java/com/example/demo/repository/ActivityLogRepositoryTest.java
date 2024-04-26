package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.domain.ActivityLog;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@DataMongoTest
public class ActivityLogRepositoryTest {
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    public static void mongoProp(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.uri", () ->   mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    public static void cleanup() {
        mongoDBContainer.stop();
    }

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Test
    public void mongoTest() {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setTxDatetime(LocalDateTime.now());
        activityLog.setStaffId("61643");
        activityLog.setBranchCode("001");
        activityLog.setChannel("Branch");
        activityLog.setRmidEc(8891481);
        activityLog.setIdType("CID");
        activityLog.setIdNo("1109299412120");
        activityLog.setServiceType("Create RM");
        activityLog.setActivityType("Dip Chip");
        activityLog.setActivityStatus("Failed");
        activityLog.setDetail(new ActivityLog.Detail("400", "Generic Server Error"));
        activityLogRepository.save(activityLog);

        Optional<ActivityLog> savedActivityLogOpt = activityLogRepository.findById(activityLog.getId());
        assertTrue(savedActivityLogOpt.isPresent());
    }
}
