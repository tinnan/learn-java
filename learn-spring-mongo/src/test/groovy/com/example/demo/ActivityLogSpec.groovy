package com.example.demo

import com.example.demo.domain.ActivityLogQueryParam
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

import java.time.LocalDateTime

class ActivityLogSpec extends ActivityLogSpecBase {
    def "findByX method"() {
        def activityLogs = activityLogRepository.findByStaffId("62007")
        expect:
        verifyAll(activityLogs) {
            size() == 2
            get(0).id == ID_2
            get(1).id == ID_3
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
                id == ID_1
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
            get(0).id == ID_3
            get(1).id == ID_1
            get(2).id == ID_4
        }

        when: "Query page 2 with descending sort"
        def activityLogsPage2 = activityLogRepository.findAll(page2)
        def activityLogs2 = activityLogsPage2.toList()

        then: "Return 1 document in sorted manner"
        activityLogsPage2.hasPrevious()
        !activityLogsPage2.hasNext()
        activityLogs2.size() == 1
        verifyAll(activityLogs2) {
            get(0).id == ID_2
        }
    }

    def "Query with search criteria"() {
        given: "Search criteria transaction date from-to and descending sorting by field 'id'"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam();
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-13T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T05:01:50")
        queryParam.getPaginationAndSort().setSortDesc("txDatetime")

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only filtered data is returned in descend sorting order"
        verifyAll(activityLogs) {
            size() == 2
            get(0).id == ID_1
            get(1).id == ID_4
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
            get(0).id == ID_4
        }
    }

    def "Query with search criteria and pagination"() {
        given: "Search criteria transaction date from-to and select 2 with page size 3"
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = LocalDateTime.parse("2024-04-12T00:00:00")
        queryParam.dateTimeTo = LocalDateTime.parse("2024-04-15T23:59:59")
        queryParam.getPaginationAndSort().setPage(2, 3)

        when: "Query data"
        def activityLogs = activityLogService.queryLogs(queryParam)

        then: "Only data on page is returned"
        verifyAll(activityLogs) {
            size() == 1
            get(0).id == ID_4
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
            get(0).id == ID_1
            get(1).id == ID_3
        }
    }
}
