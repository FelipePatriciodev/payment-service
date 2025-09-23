package com.desafio.paymentservice.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.desafio.paymentservice.domain.model.PaymentMethodType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private UUID id;
    private BigDecimal originalAmount;
    private BigDecimal appliedDiscount;
    private BigDecimal finalAmount;
    private BigDecimal cashbackAmount;
    private PaymentMethodType methodType;
    private String status; 
    private LocalDateTime createdAt;
}
