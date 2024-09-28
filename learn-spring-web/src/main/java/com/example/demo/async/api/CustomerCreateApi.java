package com.example.demo.async.api;

import com.example.demo.async.customer.model.CustomerCreateRequest;
import com.example.demo.async.customer.model.CustomerCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Customer Create API")
public interface CustomerCreateApi {

    @Operation(summary = "Create customer")
    @PostMapping("/customer")
    CustomerCreateResponse createCustomer(@RequestHeader HttpHeaders headers,
        @RequestBody @Valid CustomerCreateRequest request);
}
