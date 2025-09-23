package com.desafio.paymentservice.web.dto;

import java.util.List;

import com.desafio.paymentservice.domain.model.PaymentMethodType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PaymentRequestDto {
    @NotNull
    private PaymentMethodType paymentMethodType;

    @NotEmpty
    @Size(min = 1)
    @Valid 
    private List<ProductItemDto> products;
}
