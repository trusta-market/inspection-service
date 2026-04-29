package com.trustamarket.inspectionservice.center.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.center.application.event.SlotAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSlotEventPublisher {

    private static final String TOPIC = "center.slot-assigned";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(SlotAssignedEvent event) {
        CenterSlotAssignedEvent kafkaEvent = new CenterSlotAssignedEvent(
                event.productId(), event.sellerId(), event.centerId(),
                event.originalPriceAmount(), event.currency()
        );
        try {
            String payload = objectMapper.writeValueAsString(kafkaEvent);
            kafkaTemplate.send(TOPIC, event.productId().toString(), payload);
            log.info("center.slot-assigned 발행: productId={}, centerId={}", event.productId(), event.centerId());
        } catch (JsonProcessingException e) {
            log.error("center.slot-assigned 직렬화 실패: productId={}", event.productId(), e);
            throw new RuntimeException(e);
        }
    }
}
