package com.desafio.paymentservice.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desafio.paymentservice.domain.model.Payment;
import com.desafio.paymentservice.domain.model.PaymentMethodType;
import com.desafio.paymentservice.domain.model.PaymentStatus;
import com.desafio.paymentservice.infrastructure.repository.PaymentRepository;
import com.desafio.paymentservice.infrastructure.repository.ProductRepository;
import com.desafio.paymentservice.web.dto.PaymentRequestDto;
import com.desafio.paymentservice.web.dto.PaymentResponseDto;
import com.desafio.paymentservice.web.dto.ProductItemDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        BigDecimal originalAmount = BigDecimal.ZERO;

        for (ProductItemDto item : request.getProducts()) {
            // 🚦 Sanidade extra: quantity nunca pode ser <= 0
            if (item.getQuantity() == null || item.getQuantity().compareTo(BigDecimal.ONE) < 0) {
                throw new IllegalArgumentException("Quantidade inválida para o produto: " + item.getProductId());
            }

            var product = productRepository.findByUuid(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProductId()));

            BigDecimal itemTotal = product.getPrice()
                    .multiply(item.getQuantity());
            originalAmount = originalAmount.add(itemTotal);
        }

        BigDecimal appliedDiscount = BigDecimal.ZERO;
        BigDecimal cashbackAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = originalAmount;

        if (request.getPaymentMethodType() == PaymentMethodType.PIX) {
            appliedDiscount = originalAmount.multiply(BigDecimal.valueOf(0.05))
                    .setScale(2, RoundingMode.HALF_UP);
            finalAmount = originalAmount.subtract(appliedDiscount);
        } else if (request.getPaymentMethodType() == PaymentMethodType.CREDIT_CARD) {
            cashbackAmount = originalAmount.multiply(BigDecimal.valueOf(0.03))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        Payment payment = Payment.builder()
                .originalAmount(originalAmount)
                .finalAmount(finalAmount)
                .appliedDiscount(appliedDiscount)
                .cashbackAmount(cashbackAmount)
                .methodType(request.getPaymentMethodType())
                .status(PaymentStatus.PROCESSED)
                .build();

        paymentRepository.save(payment);

        return PaymentResponseDto.builder()
                .id(payment.getUuid())
                .originalAmount(originalAmount)
                .appliedDiscount(appliedDiscount)
                .finalAmount(finalAmount)
                .cashbackAmount(cashbackAmount)
                .methodType(request.getPaymentMethodType())
                .status("PROCESSED")
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
