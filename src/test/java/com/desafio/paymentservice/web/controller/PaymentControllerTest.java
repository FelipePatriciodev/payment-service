package com.desafio.paymentservice.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String VALID_PRODUCT_ID = "4648fb92-8b02-48d7-ac84-d844c19fb4b9";

    @Test
    void shouldProcessPaymentWithPix() throws Exception {
        String payload = """
        {
          "paymentMethodType": "PIX",
          "products": [
            { "product_id": "%s", "quantity": 2 }
          ]
        }
        """.formatted(VALID_PRODUCT_ID);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.methodType").value("PIX"))
            .andExpect(jsonPath("$.finalAmount").isNumber())
            .andExpect(jsonPath("$.status").value("PROCESSED"));
    }

    @Test
    void shouldProcessPaymentWithCreditCard() throws Exception {
        String payload = """
        {
          "paymentMethodType": "CREDIT_CARD",
          "products": [
            { "product_id": "%s", "quantity": 1 }
          ]
        }
        """.formatted(VALID_PRODUCT_ID);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.methodType").value("CREDIT_CARD"))
            .andExpect(jsonPath("$.cashbackAmount").isNumber());
    }

    @Test
    void shouldRejectPaymentWithNegativeQuantity() throws Exception {
        String payload = """
        {
          "paymentMethodType": "PIX",
          "products": [
            { "product_id": "%s", "quantity": -1 }
          ]
        }
        """.formatted(VALID_PRODUCT_ID);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPaymentWithInvalidProduct() throws Exception {
        String payload = """
        {
          "paymentMethodType": "PIX",
          "products": [
            { "product_id": "00000000-0000-0000-0000-000000000000", "quantity": 1 }
          ]
        }
        """;

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        String malformedPayload = """
        {
          "paymentMethodType": "PIX",
          "products": [
            { "product_id": "%s", "quantity":
        """.formatted(VALID_PRODUCT_ID); // JSON quebrado

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedPayload))
            .andExpect(status().isBadRequest());
    }
}
