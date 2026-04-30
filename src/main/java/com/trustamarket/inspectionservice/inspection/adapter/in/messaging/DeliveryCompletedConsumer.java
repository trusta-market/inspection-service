package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.MarkArrivedUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCompletedConsumer {

    private final MarkArrivedUseCase markArrivedUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "delivery.completed", groupId = "inspection-service")
    public void consume(String payload) {
        DeliveryCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, DeliveryCompletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("delivery.completed 이벤트 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        markArrivedUseCase.markArrived(new MarkArrivedCommand(event.productId()));
        log.info("상품 도착 확인 완료: productId={}", event.productId());
    }
}
