package com.example.demo.async.api;

import com.example.demo.async.model.ProductApplicationRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface ProductApi {

    @PostMapping("/api/v1/product/apply")
    void applyProduct(@RequestHeader HttpHeaders headers, @RequestBody ProductApplicationRequest request);
}
