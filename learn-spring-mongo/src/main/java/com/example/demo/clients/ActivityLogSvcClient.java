package com.example.demo.clients;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "svc", url = "http://localhost:8080")
public interface ActivityLogSvcClient {

    @GetMapping(value = "/api/v1/svc/logs/download")
    ResponseEntity<Resource> download(@RequestParam("txFrom") LocalDateTime txFrom,
        @RequestParam("txTo") LocalDateTime txTo)
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException;
}
