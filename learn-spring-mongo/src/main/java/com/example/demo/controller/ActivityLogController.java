package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.service.ActivityLogService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.time.LocalDateTime;
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
    public ResponseEntity<String> download()
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("Service layer received request.");
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(LocalDateTime.parse("2024-04-30T00:00:00"));
        param.setDateTimeTo(LocalDateTime.parse("2024-05-30T23:59:59"));
        return ResponseEntity.ok(activityLogService.downloadLogs(param));
    }
}
