package com.example.demo.async.product.controller;

import com.example.demo.async.api.ProductApi;
import com.example.demo.async.product.model.ProductApplicationRequest;
import com.example.demo.async.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final ProductService productService;

    public void applyProduct(HttpHeaders headers, ProductApplicationRequest request) {
        log.debug("Product {} apply request for customer email {}", request.productId(), request.customerEmail());
        productService.apply(request.customerEmail(), request.productId());
    }
}
