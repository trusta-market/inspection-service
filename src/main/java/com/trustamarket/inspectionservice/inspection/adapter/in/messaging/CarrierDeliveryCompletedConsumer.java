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
public class CarrierDeliveryCompletedConsumer {

    private final MarkArrivedUseCase markArrivedUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "carrier.delivery_completed", groupId = "inspection-service")
    public void consume(String payload) {
        CarrierDeliveryCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, CarrierDeliveryCompletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("carrier.delivery_completed 이벤트 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        markArrivedUseCase.markArrived(new MarkArrivedCommand(event.productId()));
        log.info("상품 도착 확인 완료: productId={}", event.productId());
    }
}
