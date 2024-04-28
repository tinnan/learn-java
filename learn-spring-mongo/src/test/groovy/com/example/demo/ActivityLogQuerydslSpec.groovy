package com.example.demo

import com.example.demo.domain.ActivityLogQueryParam

import java.time.LocalDateTime

class ActivityLogQuerydslSpec extends ActivityLogSpecBase {
    def "Query with search criteria"() {
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

    def "Query with more search criteria"() {
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

    def "Query with search criteria and pagination"() {
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

    def "Query with search criteria with pagination and sorting"() {
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

    def "Query with search criteria activity type (multi select)"() {
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
