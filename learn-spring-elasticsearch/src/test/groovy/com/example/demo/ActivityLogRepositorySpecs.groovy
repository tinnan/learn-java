package com.example.demo

import com.example.demo.container.ActivityLogElasticsearchContainer
import com.example.demo.model.ActivityLog
import com.example.demo.model.ActivityLogQueryParam
import com.example.demo.repository.es.ActivityLogRepository
import com.example.demo.service.ActivityLogService
import groovy.util.logging.Slf4j
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZonedDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@EnableSharedInjection
@Slf4j
class ActivityLogRepositorySpecs extends Specification {
    @Shared
    static ActivityLogElasticsearchContainer elasticsearchContainer = new ActivityLogElasticsearchContainer()
//    @Shared
//    @Autowired
//    ElasticsearchOperations elasticsearchOperations
    @Shared
    @Autowired
    ActivityLogRepository activityLogRepository
    @Shared
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Shared
    @Autowired
    ActivityLogService activityLogService;

    @DynamicPropertySource
    static def propertySetup(DynamicPropertyRegistry registry) {
        elasticsearchContainer.start()
        def port = elasticsearchContainer.getMappedPort(9200)
        registry.add("spring.elasticsearch.uris", () -> "http://localhost:" + port)
        registry.add("spring.data.elasticsearch.repositories.enabled", () -> "true")
    }

    def setupSpec() {
        assert elasticsearchContainer.isRunning()
        recreateIndex()
        insertData()
    }

    def cleanupSpec() {
        elasticsearchContainer.stop()
    }

    def "when context is loaded, all expected bean are created"() {
        expect:
        activityLogRepository
        elasticsearchTemplate
    }

    def "find all should return all activity logs"() {
        when:
        def activityLogs = activityLogRepository.findAll()

        then:
        with(activityLogs) {
            size() == 4
            verifyAll(get(0)) {
                id == "1"
                txDatetime == ZonedDateTime.parse("2024-04-15T10:41:33+07:00")
                staffId == "52134"
                branchCode == "001"
                channel == "Branch"
                rmidEc == 77318491
                idType == "CID"
                idNo == "1123900091841"
                serviceType == "Create RM"
                activityType == "Dip Chip"
                activityStatus == "Failed"
                verifyAll(detail) {
                    errorCode == "400"
                    errorMsg == "Generic Server Error"
                }
            }
        }
    }

    def "Query with pagination and sorting should return data page by page"() {
        def page1 = PageRequest.of(0, 3, Sort.by("txDatetime").descending())
        def page2 = PageRequest.of(1, 3, Sort.by("txDatetime").descending())

        when: "Query page 1 with descending sort"
        def activityLogsPage1 = activityLogRepository.findAll(page1)
        def activityLogs1 = activityLogsPage1.toList()

        then: "Return 3 documents in sorted manner"
        !activityLogsPage1.hasPrevious()
        activityLogsPage1.hasNext()
        activityLogs1.size() == 3
        verifyAll(activityLogs1) {
            get(0).id == "3"
            get(1).id == "1"
            get(2).id == "4"
        }

        when: "Query page 2 with descending sort"
        def activityLogsPage2 = activityLogRepository.findAll(page2)
        def activityLogs2 = activityLogsPage2.toList()

        then: "Return 1 document in sorted manner"
        activityLogsPage2.hasPrevious()
        !activityLogsPage2.hasNext()
        activityLogs2.size() == 1
        verifyAll(activityLogs2) {
            get(0).id == "2"
        }
    }

    def "Query with search criteria"() {
        given: "Search criteria transaction date from-to and descending sorting by field 'id'"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam();
        queryParam.dateTimeFrom = ZonedDateTime.parse("2024-04-13T07:00:00+07:00")
        queryParam.dateTimeTo = ZonedDateTime.parse("2024-04-15T12:01:50+07:00")
        queryParam.createPaginationAndSort().setSortDesc("id")

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only filtered data is returned in descend sorting order"
        verifyAll(activityLogs) {
            size() == 2
            get(0).id == "4"
            get(1).id == "1"
        }
    }

    def "Query with more search criteria"() {
        given: "Search criteria transaction date from-to and activity status = Pass"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = ZonedDateTime.parse("2024-04-13T07:00:00+07:00")
        queryParam.dateTimeTo = ZonedDateTime.parse("2024-04-15T12:01:50+07:00")
        queryParam.activityStatus = "Pass"

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)
        log.info("Logs: {}", activityLogs)

        then: "Only filtered data is returned"
        verifyAll(activityLogs) {
            size() == 1
            get(0).id == "4"
        }
    }

    def "Query with search criteria and pagination"() {
        given: "Search criteria transaction date from-to and select 2 with page size 3"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T07:00:00+07:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-16T06:59:59+07:00")
        queryParam.createPaginationAndSort().setPage(2, 3)

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only data on page is returned"
        verifyAll(activityLogs) {
            size() == 1
            get(0).id == "4"
        }
    }

    def "Query with search criteria activity type (multi select)"() {
        given: "Search criteria transaction date from-to and activity type 'Dip Chip', 'Personal Info Input'"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T07:00:00+07:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-16T06:59:59+07:00")
        queryParam.activityType = List.of("Dip Chip", "Personal Info Input")

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only filtered data is returned"
        verifyAll(activityLogs) {
            size() == 2
            get(0).id == "1"
            get(1).id == "3"
        }
    }

    private def insertData() {
        ActivityLog activityLog1 = new ActivityLog()
        activityLog1.id = "1"
        activityLog1.txDatetime = ZonedDateTime.parse("2024-04-15T10:41:33+07:00")
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
        activityLog2.id = "2"
        activityLog2.txDatetime = ZonedDateTime.parse("2024-04-12T13:03:12+07:00")
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
        activityLog3.id = "3"
        activityLog3.txDatetime = ZonedDateTime.parse("2024-04-15T13:12:01+07:00")
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
        activityLog4.id = "4"
        activityLog4.txDatetime = ZonedDateTime.parse("2024-04-14T17:54:34+07:00")
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

    private def recreateIndex() {
        if (elasticsearchTemplate.indexOps(ActivityLog).exists()) {
            elasticsearchTemplate.indexOps(ActivityLog).delete()
            elasticsearchTemplate.indexOps(ActivityLog).create()
        }
    }
}
