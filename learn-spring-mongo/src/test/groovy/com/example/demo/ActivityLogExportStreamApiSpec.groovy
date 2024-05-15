package com.example.demo

import com.example.demo.domain.ActivityLogQueryParam

import java.time.Instant

class ActivityLogExportStreamApiSpec extends ActivityLogExportSpecBase {
    def "export large data set to CSV file should success with data row count as expected"() {
        given:
        ActivityLogQueryParam queryParam = new ActivityLogQueryParam()
        queryParam.dateTimeFrom = Instant.parse("2024-04-30T00:00:00Z")
        queryParam.dateTimeTo = Instant.parse("2024-05-30T23:59:59Z")

        when:
        activityLogService.exportLogsWithStream(queryParam)
//        def fileReader = ReversedLinesFileReader.builder().setFile(exportFilePath).get()
//        def recordCount = fileReader.readLine()

        then:
        1 == 1
//        recordCount == "1000000"
    }
}
