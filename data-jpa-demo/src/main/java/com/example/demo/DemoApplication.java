package com.example.demo;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Collection;

@SpringBootApplication(scanBasePackages = "com.example.demo")
@Slf4j
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    @Profile("mongo")
    public CommandLineRunner runMongoApp(ProductService productService) {
        return args -> {
            productService.registerProduct(new Product("Toy gun",
                    "Toy gun able to shoot non-toxic, child friendly rubber projectiles.",
                    new Product.Dimension(10.0, 4.0, 9.0),
                    5.99));
            productService.registerProduct(new Product("Game pad",
                    "Wired controller for your best gaming experience. Compatible with all major game consoles.",
                    new Product.Dimension(4.5, 2.0, 1.0),
                    10.99));
            Collection<Product> products = productService.listProducts();
            log.info("All products: {}", products);
        };
    }
}
