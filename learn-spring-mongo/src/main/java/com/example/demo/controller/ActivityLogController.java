package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.domain.ActivityLogResponse;
import com.example.demo.service.ActivityLogService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
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
    public ResponseEntity<ActivityLogResponse> download(LocalDateTime txFrom, LocalDateTime txTo)
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("Service layer received request.");
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(txFrom);
        param.setDateTimeTo(txTo);
        List<ActivityLog> activityLogs = activityLogService.queryLogs(param);
        return ResponseEntity.ok(new ActivityLogResponse(activityLogs));
    }
}
