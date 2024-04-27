package com.example.demo

import com.example.demo.domain.ActivityLog
import com.example.demo.domain.ActivityLogQueryParam
import com.example.demo.repository.ActivityLogRepository
import com.example.demo.service.ActivityLogService
import groovy.util.logging.Slf4j
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
class ActivityLogSpec extends Specification {
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

    def "findByX method"() {
        def activityLogs = activityLogRepository.findByStaffId("62007")
        expect:
        verifyAll(activityLogs) {
            size() == 2
            get(0).id == 2
            get(1).id == 3
        }
    }

    def "Find all should return all activity logs in collection"() {
        when:
        def activityLogs = activityLogRepository.findAll()

        then:
        with(activityLogs) {
            !empty
            size() == 4
            verifyAll(get(0)) {
                id == 1
                txDatetime == LocalDateTime.parse("2024-04-15T03:41:33")
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

    def "Count should return number of document in collection"() {
        expect:
        activityLogRepository.count() == 4
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
            get(0).id == 3
            get(1).id == 1
            get(2).id == 4
        }

        when: "Query page 2 with descending sort"
        def activityLogsPage2 = activityLogRepository.findAll(page2)
        def activityLogs2 = activityLogsPage2.toList()

        then: "Return 1 document in sorted manner"
        activityLogsPage2.hasPrevious()
        !activityLogsPage2.hasNext()
        activityLogs2.size() == 1
        verifyAll(activityLogs2) {
            get(0).id == 2
        }
    }

    def "Query with search criteria"() {
        given: "Search criteria transaction date from-to and descending sorting by field 'id'"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam();
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-13T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T05:01:50")
        queryParam.createPaginationAndSort().setSortDesc("id")

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only filtered data is returned in descend sorting order"
        verifyAll(activityLogs) {
            size() == 2
            get(0).id == 4
            get(1).id == 1
        }
    }

    def "Query with more search criteria"() {
        given: "Search criteria transaction date from-to and activity status = Pass"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-13T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T05:01:50")
        queryParam.activityStatus = "Pass"

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only filtered data is returned"
        verifyAll(activityLogs) {
            size() == 1
            get(0).id == 4
        }
    }

    def "Query with search criteria and pagination"() {
        given: "Search criteria transaction date from-to and select 2 with page size 3"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T23:59:59")
        queryParam.createPaginationAndSort().setPage(2, 3)

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only data on page is returned"
        verifyAll(activityLogs) {
            size() == 1
            get(0).id == 4
        }
    }

    def "Query with search criteria activity type (multi select)"() {
        given: "Search criteria transaction date from-to and activity type 'Dip Chip', 'Personal Info Input'"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T23:59:59")
        queryParam.activityType = List.of("Dip Chip", "Personal Info Input")

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only filtered data is returned"
        verifyAll(activityLogs) {
            size() == 2
            get(0).id == 1
            get(1).id == 3
        }
    }

    def "QueryDsl with search criteria"() {
        given: "Search criteria transaction date from-to and descending sorting by field 'id'"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam();
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-13T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T05:01:50")
        queryParam.createPaginationAndSort().setSortDesc("id");

        when: "Query data"
        def pair = activityLogService.queryDslLogs(queryParam)
        def page = pair.first
        def logs = pair.second

        then: "Only filtered data is returned in descend sorting order"
        verifyAll {
            page.pageable.unpaged
            page.pageable.sort.descending().sorted
            logs.size() == 2
            logs.get(0).id == 4
            logs.get(1).id == 1
        }
    }

    def "QueryDsl with more search criteria"() {
        given: "Search criteria transaction date from-to and activity status = Pass"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-13T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T05:01:50")
        queryParam.activityStatus = "Pass"

        when: "Query data"
        def pair = activityLogService.queryDslLogs(queryParam)
        def page = pair.first
        def logs = pair.second

        then: "Only filtered data is returned"
        verifyAll {
            page.pageable.unpaged
            page.sort.unsorted
            logs.size() == 1
            logs.get(0).id == 4
        }
    }

    def "QueryDsl with search criteria and pagination"() {
        given: "Search criteria transaction date from-to and select 2 with page size 3"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T23:59:59")
        queryParam.createPaginationAndSort().setPage(2, 3)

        when: "Query data"
        def pair = activityLogService.queryDslLogs(queryParam)
        def page = pair.first
        def logs = pair.second

        then: "Only data on page is returned"
        verifyAll {
            page.pageable.paged
            page.totalPages == 2
            page.last
            page.pageable.pageNumber == 1 // Zero-based index.
            page.sort.unsorted
            logs.size() == 1
            logs.get(0).id == 4
        }
    }

    def "QueryDsl with search criteria with pagination and sorting"() {
        given: "Search criteria transaction date from-to and select 2 with page size 3"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T23:59:59")
        queryParam.createPaginationAndSort().setPage(1, 2).setSortAcs("rmidEc")

        when: "Query data"
        def pair = activityLogService.queryDslLogs(queryParam)
        def page = pair.first
        def logs = pair.second

        then: "Only data on page is returned"
        verifyAll {
            page.pageable.paged
            page.totalPages == 2
            page.first
            page.pageable.pageNumber == 0 // Zero-based index.
            page.sort.ascending().sorted
            logs.size() == 2
            logs.get(0).id == 3
            logs.get(1).id == 1
        }
    }

    def "QueryDsl with search criteria activity type (multi select)"() {
        given: "Search criteria transaction date from-to and activity type 'Dip Chip', 'Personal Info Input'"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T23:59:59")
        queryParam.activityType = List.of("Dip Chip", "Personal Info Input")

        when: "Query data"
        def pair = activityLogService.queryDslLogs(queryParam)
        def logs = pair.second

        then: "Only filtered data is returned"
        verifyAll {
            logs.size() == 2
            logs.get(0).id == 1
            logs.get(1).id == 3
        }
    }
}
