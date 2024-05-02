package com.example.demo.controller;

import com.example.demo.domain.ActivityLogQueryParam;
import com.example.demo.service.ActivityLogService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping(value = "/download", produces = "text/csv")
    public ResponseEntity<String> download()
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        ActivityLogQueryParam param = new ActivityLogQueryParam();
        param.setDateTimeFrom(LocalDateTime.parse("2024-04-30T00:00:00"));
        param.setDateTimeTo(LocalDateTime.parse("2024-05-30T23:59:59"));

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-disposition", "attachment; filename=\"" + "activity_logs_" + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv\"");
        return new ResponseEntity<>(activityLogService.downloadLogs(param), headers, HttpStatus.OK);
    }
}
