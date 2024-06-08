package com.example.demo.clients;

import com.example.demo.domain.activitylog.ActivityLog;
import com.example.demo.domain.activitylog.ActivityLogResponse;
import com.example.demo.domain.activitylog.ActivityLogWithPageResponse;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.time.Instant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "svc", url = "http://localhost:8080")
public interface ActivityLogSvcClient {

    @GetMapping(value = "/api/v1/svc/logs/download")
    ResponseEntity<ActivityLogResponse> download(
        @RequestParam("txFrom") Instant txFrom,
        @RequestParam("txTo") Instant txTo)
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException;

    @GetMapping(value = "/api/v1/svc/logs/generate")
    ResponseEntity<String> generate(
        @RequestParam("txFrom") Instant txFrom,
        @RequestParam("txTo") Instant txTo
    );

    @GetMapping(value = "/api/v1/svc/logs")
    ResponseEntity<ActivityLogWithPageResponse> queryWithPage(
        @RequestParam("txFrom") Instant txFrom,
        @RequestParam("txTo") Instant txTo,
        @RequestParam("page") Integer pageNumber,
        @RequestParam("pageSize") Integer pageSize);

    @GetMapping(value = "/api/v1/svc/logs/view")
    ResponseEntity<ActivityLogWithPageResponse> queryWithView(
        @RequestParam("txFrom") Instant txFrom,
        @RequestParam("txTo") Instant txTo,
        @RequestParam(name = "userActivity", required = false) String userActivity,
        @RequestParam("page") Integer pageNumber,
        @RequestParam("pageSize") Integer pageSize
    );

    @GetMapping(value = "/api/v1/svc/logs/agg")
    ResponseEntity<ActivityLogWithPageResponse> queryWithAgg(
        @RequestParam("txFrom") Instant txFrom,
        @RequestParam("txTo") Instant txTo,
        @RequestParam(name = "userActivity", required = false) String userActivity,
        @RequestParam("page") Integer pageNumber,
        @RequestParam("pageSize") Integer pageSize
    );

    @PostMapping(value = "/api/v1/svc/logs")
    ResponseEntity<ActivityLog> create(@RequestBody ActivityLog activityLog);
}
