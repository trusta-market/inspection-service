package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaInspectionFailedProducer {

    private static final String TOPIC = "inspection.failed";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(InspectionFailedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(
                    new InspectionFailedKafkaEvent(event.inspectionId(), event.productId(), event.sellerId())
            );
            kafkaTemplate.send(TOPIC, event.inspectionId().toString(), payload);
            log.info("inspection.failed 발행: inspectionId={}, sellerId={}", event.inspectionId(), event.sellerId());
        } catch (JsonProcessingException e) {
            log.error("inspection.failed 직렬화 실패: inspectionId={}", event.inspectionId(), e);
            throw new RuntimeException(e);
        }
    }
}
