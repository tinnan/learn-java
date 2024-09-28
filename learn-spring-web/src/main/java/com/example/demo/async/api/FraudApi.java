package com.example.demo.async.api;

import com.example.demo.async.fraud.model.FraudCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Fraud API")
public interface FraudApi {

    @Operation(summary = "Check fraud")
    @GetMapping("/check-fraud")
    FraudCheckResponse isFraudster(@RequestHeader HttpHeaders headers, @RequestParam("customerId") Integer customerId);
}
