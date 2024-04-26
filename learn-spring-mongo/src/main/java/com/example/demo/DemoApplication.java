package com.example.demo;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLog.Detail;
import com.example.demo.repository.ActivityLogRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@Slf4j
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner run(ActivityLogRepository activityLogRepository) {
        return args -> {
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
            activityLog.setDetail(new Detail("400", "Generic Server Error"));
            activityLogRepository.save(activityLog);
            log.info("Activity log saved.");
        };
    }
}
