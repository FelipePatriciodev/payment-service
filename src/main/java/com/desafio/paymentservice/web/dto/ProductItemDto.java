package com.desafio.paymentservice.web.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class ProductItemDto {

    @NotNull
    @JsonProperty("product_id") // garante compatibilidade com JSON snake_case
    private String productId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que 0")
    @Digits(integer = 10, fraction = 0, message = "Quantidade deve ser um número inteiro")
    private BigDecimal quantity;
}
