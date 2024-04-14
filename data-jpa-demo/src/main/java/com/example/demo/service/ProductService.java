package com.example.demo.service;

import com.example.demo.dao.ProductRepository;
import com.example.demo.domain.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public Product registerProduct(Product product) {
        product.setId(UUID.randomUUID().toString());
        return productRepository.save(product);
    }

    public Collection<Product> listProducts() {
        return productRepository.findAll();
    }
}
