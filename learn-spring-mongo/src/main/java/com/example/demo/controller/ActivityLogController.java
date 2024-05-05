package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.ActivityLogResponse;
import com.example.demo.service.ActivityLogService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ActivityLogController implements ActivityLogSvcClient {

    private final ActivityLogService activityLogService;

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
}
