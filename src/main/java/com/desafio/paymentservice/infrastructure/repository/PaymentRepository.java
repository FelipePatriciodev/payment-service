package com.desafio.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desafio.paymentservice.domain.model.Payment;
import com.desafio.paymentservice.domain.model.Product;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Product> findByUuid(String uuid);
}
