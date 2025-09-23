package com.desafio.paymentservice.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.desafio.paymentservice.web.dto.PaymentEventDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private static final String TOPIC = "payments";

    private final KafkaTemplate<String, PaymentEventDto> kafkaTemplate;

    public void sendPaymentEvent(PaymentEventDto event) {
        kafkaTemplate.send(TOPIC, event);
        log.info("Evento de pagamento publicado no Kafka: {}", event);
    }
}
