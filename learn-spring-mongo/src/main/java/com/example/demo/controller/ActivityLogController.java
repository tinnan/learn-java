package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.ActivityLogQueryParam.PaginationAndSort;
import com.example.demo.domain.ActivityLogResponse;
import com.example.demo.domain.ActivityLogWithPageResponse;
import com.example.demo.domain.ActivityLogWithPageResponse.PageInfo;
import com.example.demo.service.ActivityLogAggregationService;
import com.example.demo.service.ActivityLogPaginationService;
import com.example.demo.service.ActivityLogService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ActivityLogController implements ActivityLogSvcClient {

    private final ActivityLogService activityLogService;
    private final ActivityLogPaginationService activityLogPaginationService;
    private final ActivityLogAggregationService activityLogAggregationService;

    @Override
    public ResponseEntity<ActivityLogResponse> download(LocalDateTime txFrom, LocalDateTime txTo) {
        log.info("Service layer received request - download.");
        log.info("Query: Tx {} - {}", txFrom, txTo);
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        List<ActivityLog> activityLogs = activityLogService.queryLogs(param);
        return ResponseEntity.ok(new ActivityLogResponse(activityLogs));
    }

    @Override
    public ResponseEntity<String> generate(LocalDateTime txFrom, LocalDateTime txTo) {
        log.info("Service layer received request - generate.");
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        final String exportFilePath = activityLogService.exportLogsWithStream(param);
        return ResponseEntity.ok(exportFilePath);
    }

    @Override
    public ResponseEntity<ActivityLogWithPageResponse> queryWithPage(LocalDateTime txFrom, LocalDateTime txTo,
        Integer pageNumber, Integer pageSize) {
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);

        Page<ActivityLog> activityLogPage = activityLogPaginationService.findWithPage(param, pageNumber, pageSize);
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize,
            activityLogPage.getTotalPages(),
            activityLogPage.getTotalElements(),
            activityLogPage.hasPrevious(),
            activityLogPage.hasNext(),
            activityLogPage.isLast()
        );
        ActivityLogWithPageResponse response = new ActivityLogWithPageResponse(
            activityLogPage.getContent(), pageInfo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ActivityLogWithPageResponse> queryWithAgg(LocalDateTime txFrom, LocalDateTime txTo,
        String serviceType, String activityStatus, Integer pageNumber, Integer pageSize) {
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        param.setServiceType(serviceType);
        param.setActivityStatus(activityStatus);
        PaginationAndSort paginationAndSort = param.createPaginationAndSort();
        paginationAndSort.setPage(pageNumber, pageSize);

        List<ActivityLog> activityLogs = activityLogAggregationService.queryAggregation(param);
        ActivityLogWithPageResponse response = new ActivityLogWithPageResponse(activityLogs, null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ActivityLog> create(ActivityLog activityLog) {
        ActivityLog saved = activityLogService.create(activityLog);
        return ResponseEntity.ok(saved);
    }
}
