package com.desafio.paymentservice.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEventDto {
    private String id;             
    private String status;
    private String methodType;
    private BigDecimal originalAmount;
    private BigDecimal appliedDiscount;
    private BigDecimal finalAmount;
    private BigDecimal cashbackAmount;
    private String createdAt;      
}
