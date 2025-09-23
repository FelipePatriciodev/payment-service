package com.desafio.paymentservice.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private static final String TOPIC = "payments";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPaymentEvent(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
