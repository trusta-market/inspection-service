package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaInspectionCompletedProducer {

    private static final String INSPECTION_COMPLETED_TOPIC = "inspection.completed";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(InspectionCompletedEvent event) {
        publish(INSPECTION_COMPLETED_TOPIC, event.inspectionId().toString(),
                new InspectionCompletedKafkaEvent(
                        event.inspectionId(), event.productId(),
                        event.grade(), event.suggestedPriceAmount(),
                        event.currency(), event.inspectorId()
                ));
    }

    private void publish(String topic, String key, Object payload) {
        try {
            kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(payload));
            log.info("{} 발행: key={}", topic, key);
        } catch (JsonProcessingException e) {
            log.error("{} 직렬화 실패: key={}", topic, key, e);
            throw new RuntimeException(e);
        }
    }
}
