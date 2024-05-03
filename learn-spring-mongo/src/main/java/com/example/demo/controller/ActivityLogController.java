package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.service.ActivityLogService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ActivityLogController implements ActivityLogSvcClient {

    private final ActivityLogService activityLogService;

    @Override
    public ResponseEntity<Resource> download()
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("Service layer received request.");
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(LocalDateTime.parse("2024-04-30T00:00:00"));
        param.setDateTimeTo(LocalDateTime.parse("2024-05-30T23:59:59"));
        ByteArrayResource resource = new ByteArrayResource(activityLogService.downloadLogs(param).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + "activity_logs_" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv\"");
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
