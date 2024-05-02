package com.example.demo.clients;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "svc", url = "http://localhost:8080")
public interface ActivityLogSvcClient {

    @GetMapping(value = "/api/v1/svc/logs/download")
    ResponseEntity<String> download() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException;
}
