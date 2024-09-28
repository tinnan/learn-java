package com.example.demo.async.api;

import com.example.demo.async.model.ProductApplicationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Product API")
public interface ProductApi {

    @Operation(summary = "Apply product")
    @PostMapping("/product/apply")
    void applyProduct(@RequestHeader HttpHeaders headers, @RequestBody ProductApplicationRequest request);
}
