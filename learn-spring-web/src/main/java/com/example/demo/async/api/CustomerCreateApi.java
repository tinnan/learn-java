package com.example.demo.async.api;

import com.example.demo.async.model.CustomerCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Customer Create API")
public interface CustomerCreateApi {

    @Operation(summary = "Create customer")
    @PostMapping("/customer")
    CustomerCreateResponse createCustomer(@RequestHeader HttpHeaders headers,
        @RequestParam String customerEmail);
}
