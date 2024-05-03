package com.example.demo.controller;

import com.example.demo.clients.ActivityLogSvcClient;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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
        ResponseEntity<Resource> logData = activityLogSvcClient.download();
        log.info("Headers: {}", logData.getHeaders());
        return ResponseEntity.ok().headers(logData.getHeaders()).body(logData.getBody());
    }
}
