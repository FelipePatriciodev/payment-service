package com.desafio.paymentservice.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.desafio.paymentservice.web.dto.PaymentEventDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentConsumer {

	@KafkaListener(topics = "payments", groupId = "payment-service-group")
	public void consume(PaymentEventDto event) {
	    log.info("Evento de pagamento consumido do Kafka. Detalhes: {{ id: '{}', status: '{}', methodType: '{}', originalAmount: {}, appliedDiscount: {}, finalAmount: {}, cashbackAmount: {}, createdAt: '{}' }}",
	            event.getId(),
	            event.getStatus(),
	            event.getMethodType(),
	            event.getOriginalAmount(),
	            event.getAppliedDiscount(),
	            event.getFinalAmount(),
	            event.getCashbackAmount(),
	            event.getCreatedAt()
	    );
	}

}
