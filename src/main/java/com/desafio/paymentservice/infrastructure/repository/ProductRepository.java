package com.desafio.paymentservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desafio.paymentservice.domain.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}