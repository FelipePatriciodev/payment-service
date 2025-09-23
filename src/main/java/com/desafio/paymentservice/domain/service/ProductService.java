package com.desafio.paymentservice.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.desafio.paymentservice.domain.model.Product;
import com.desafio.paymentservice.infrastructure.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
