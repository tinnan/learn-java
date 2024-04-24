package com.example.demo.product;

import com.example.demo.model.ProductApplicationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PostMapping("/api/v1/product/apply")
    public void applyProduct(@RequestBody ProductApplicationRequest request) {
        productService.apply(request.customerId(), request.productId());
    }
}
