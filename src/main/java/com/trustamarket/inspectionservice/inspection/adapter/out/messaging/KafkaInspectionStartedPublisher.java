package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaInspectionStartedPublisher {

    private static final String TOPIC = "inspection.started";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(InspectionStartedEvent event) {
        InspectionStartedKafkaEvent kafkaEvent = new InspectionStartedKafkaEvent(
                event.inspectionId(), event.productId()
        );
        try {
            String payload = objectMapper.writeValueAsString(kafkaEvent);
            kafkaTemplate.send(TOPIC, event.inspectionId().toString(), payload);
            log.info("inspection.started 발행: inspectionId={}, productId={}", event.inspectionId(), event.productId());
        } catch (JsonProcessingException e) {
            log.error("inspection.started 직렬화 실패: inspectionId={}", event.inspectionId(), e);
            throw new RuntimeException(e);
        }
    }
}
