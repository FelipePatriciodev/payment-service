package com.desafio.paymentservice.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentConsumer {

    @KafkaListener(topics = "payments", groupId = "payment-service-group")
    public void consume(String message) {
        log.info("Evento recebido do Kafka: {}", message);
    }
}
