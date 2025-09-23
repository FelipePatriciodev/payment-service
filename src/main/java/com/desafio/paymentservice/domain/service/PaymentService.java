package com.desafio.paymentservice.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

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

        // 1. Calcular valor original
        for (ProductItemDto item : request.getProducts()) {
            var product = productRepository.findByUuid(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProductId()));

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            originalAmount = originalAmount.add(itemTotal);
        }

        BigDecimal appliedDiscount = BigDecimal.ZERO;
        BigDecimal cashbackAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = originalAmount;

        // 2. Aplicar regras de pagamento
        if (request.getPaymentMethodType() == PaymentMethodType.PIX) {
            appliedDiscount = originalAmount.multiply(BigDecimal.valueOf(0.05))
                    .setScale(2, RoundingMode.HALF_UP);
            finalAmount = originalAmount.subtract(appliedDiscount);
        } else if (request.getPaymentMethodType() == PaymentMethodType.CREDIT_CARD) {
            cashbackAmount = originalAmount.multiply(BigDecimal.valueOf(0.03))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (request.getPaymentMethodType() == PaymentMethodType.DEBIT_CARD) {
            // nenhuma regra extra
        } else {
            throw new IllegalArgumentException("Método de pagamento inválido");
        }

        // 3. Criar entidade Payment
        Payment payment = Payment.builder()
                .uuid(UUID.randomUUID())
                .originalAmount(originalAmount)
                .finalAmount(finalAmount)
                .cashbackAmount(cashbackAmount)
                .appliedDiscount(appliedDiscount)
                .methodType(request.getPaymentMethodType())
                .status(PaymentStatus.PROCESSED)
                .build();

        paymentRepository.save(payment);

        // 4. Retornar DTO de resposta conforme doc
        return PaymentResponseDto.builder()
                .id(payment.getUuid())
                .originalAmount(originalAmount)
                .appliedDiscount(appliedDiscount)
                .finalAmount(finalAmount)
                .cashbackAmount(cashbackAmount)
                .methodType(request.getPaymentMethodType())
                .status("PROCESSED")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
