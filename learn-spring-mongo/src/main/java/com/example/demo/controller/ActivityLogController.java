package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.example.demo.domain.activitylog.ActivityLog;
import com.example.demo.domain.activitylog.ActivityLogForAggregate;
import com.example.demo.domain.activitylog.ActivityLogQueryParam;
import com.example.demo.domain.activitylog.ActivityLogQueryParam.PaginationAndSort;
import com.example.demo.domain.activitylog.ActivityLogResponse;
import com.example.demo.domain.activitylog.ActivityLogView;
import com.example.demo.domain.activitylog.ActivityLogWithPageResponse;
import com.example.demo.domain.activitylog.ActivityLogWithPageResponse.PageInfo;
import com.example.demo.service.ActivityLogAggregationService;
import com.example.demo.service.ActivityLogPaginationService;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.ActivityLogViewService;
import java.time.Instant;
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
    private final ActivityLogViewService activityLogViewService;

    @Override
    public ResponseEntity<ActivityLogResponse> download(Instant txFrom, Instant txTo) {
        log.info("Service layer received request - download.");
        log.info("Query: Tx {} - {}", txFrom, txTo);
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        List<ActivityLog> activityLogs = activityLogService.queryLogs(param);
        return ResponseEntity.ok(new ActivityLogResponse(activityLogs));
    }

    @Override
    public ResponseEntity<String> generate(Instant txFrom, Instant txTo) {
        log.info("Service layer received request - generate.");
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        final String exportFilePath = activityLogService.exportLogsWithStream(param);
        return ResponseEntity.ok(exportFilePath);
    }

    @Override
    public ResponseEntity<ActivityLogWithPageResponse> queryWithPage(Instant txFrom, Instant txTo,
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
    public ResponseEntity<ActivityLogWithPageResponse> queryWithView(Instant txFrom, Instant txTo,
        String userActivity, Integer pageNumber, Integer pageSize) {
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        param.setUserActivity(userActivity);
        param.getPaginationAndSort().setPage(pageNumber, pageSize);

        Page<ActivityLogView> page = activityLogViewService.query(param);
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize, page.getTotalPages(), page.getTotalElements(),
            page.hasPrevious(), page.hasNext(), page.isLast()
        );
        ActivityLogWithPageResponse response = new ActivityLogWithPageResponse(page.getContent(), pageInfo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ActivityLogWithPageResponse> queryWithAgg(Instant txFrom, Instant txTo,
        String userActivity, Integer pageNumber, Integer pageSize) {
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        param.setUserActivity(userActivity);
        PaginationAndSort paginationAndSort = param.getPaginationAndSort();
        paginationAndSort.setPage(pageNumber, pageSize);

        List<ActivityLogForAggregate> activityLogs = activityLogAggregationService.queryAggregation(param);
        ActivityLogWithPageResponse response = new ActivityLogWithPageResponse(activityLogs, null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ActivityLog> create(ActivityLog activityLog) {
        ActivityLog saved = activityLogService.create(activityLog);
        return ResponseEntity.ok(saved);
    }
}
