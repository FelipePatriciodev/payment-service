package com.desafio.paymentservice.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.desafio.paymentservice.domain.model.Product;
import com.desafio.paymentservice.infrastructure.repository.ProductRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.saveAll(List.of(
                        Product.builder().name("Teclado Mecânico").price(new BigDecimal("250.00")).build(),
                        Product.builder().name("Mouse Gamer").price(new BigDecimal("150.00")).build(),
                        Product.builder().name("Monitor 24\"").price(new BigDecimal("900.00")).build(),
                        Product.builder().name("Headset").price(new BigDecimal("350.00")).build(),
                        Product.builder().name("Cadeira Gamer").price(new BigDecimal("1200.00")).build()
                ));
            }
        };
    }
}
