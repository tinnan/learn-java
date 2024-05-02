package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BffController {

    private final ActivityLogSvcClient activityLogSvcClient;

    @GetMapping(value = "/api/v1/bff/logs/download", produces = "text/csv")
    public ResponseEntity<Resource> download()
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        log.info("BFF layer received request.");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + "activity_logs_" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv\"");
        ResponseEntity<String> logData = activityLogSvcClient.download();
        ByteArrayResource resource = new ByteArrayResource(Objects.requireNonNull(logData.getBody()).getBytes());
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }
}
