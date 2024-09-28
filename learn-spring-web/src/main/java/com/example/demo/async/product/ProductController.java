package com.example.demo.async.product;

import com.example.demo.async.api.ProductApi;
import com.example.demo.async.model.ProductApplicationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final ProductService productService;

    public void applyProduct(HttpHeaders headers, ProductApplicationRequest request) {
        log.info("Product {} apply request for customer email {}", request.productId(), request.customerEmail());
        productService.apply(headers, request.customerEmail(), request.productId());
    }
}
