package com.example.demo.repository.es;

import com.example.demo.container.ActivityLogElasticsearchContainer;
import com.example.demo.model.ActivityLog;
import com.example.demo.model.ActivityLogQueryParam;
import com.example.demo.service.ActivityLogService;
import groovy.util.logging.Slf4j;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.spock.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@Slf4j
public class ActivityLogRepositoryTests {

    private static final Logger log = LoggerFactory.getLogger(ActivityLogRepositoryTests.class);
    static ActivityLogElasticsearchContainer elasticsearchContainer = new ActivityLogElasticsearchContainer();
    @Autowired
    ActivityLogService activityLogService;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        Integer mappedPort = elasticsearchContainer.getMappedPort(9200);
        log.info("Mapped port: {}", mappedPort);
//        registry.add("spring.elasticsearch.username", () -> "elastic");
//        registry.add("spring.elasticsearch.password", () -> "changeit");
        registry.add("spring.elasticsearch.uris", () -> "http://localhost:" + mappedPort);
    }

    @BeforeAll
    static void beforeAll() {
        log.info("Before all");
        elasticsearchContainer.start();
    }

    @AfterAll
    static void afterAll() {
        elasticsearchContainer.stop();
    }

    @Test
    public void test() {
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam();
        queryParam.setDateTimeFrom(ZonedDateTime.parse("2024-04-13T07:00:00+07:00"));
        queryParam.setDateTimeTo(ZonedDateTime.parse("2024-04-15T12:01:50+07:00"));
        queryParam.createPaginationAndSort().setSortDesc("id");

        List<ActivityLog> activityLogs = activityLogService.queryLogs(queryParam);
        log.info("Activity Logs: {}", activityLogs);
    }
}
