package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.example.demo.domain.ActivityLogResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BffController {

    private final ActivityLogSvcClient activityLogSvcClient;
    private final ActivityLogService activityLogService;

    @GetMapping(value = "/api/v1/bff/logs/download", produces = "text/csv")
    public ResponseEntity<Resource> download(@RequestParam("txFrom") LocalDateTime txFrom,
        @RequestParam("txTo") LocalDateTime txTo)
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("BFF layer received request.");
        log.info("Query: Tx {} - {}", txFrom, txTo);
        ResponseEntity<ActivityLogResponse> logData = activityLogSvcClient.download(txFrom, txTo);
        String csvText = activityLogService.beanToCsvText(logData.getBody().activityLogs());

        ByteArrayResource resource = new ByteArrayResource(csvText.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + "activity_logs_" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv\"");
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
