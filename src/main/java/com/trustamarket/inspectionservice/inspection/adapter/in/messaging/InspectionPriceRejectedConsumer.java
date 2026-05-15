package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RejectPriceCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RejectPriceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionPriceRejectedConsumer {

    private final RejectPriceUseCase rejectPriceUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.price.rejected", groupId = "inspection-service")
    public void consume(String payload) {
        InspectionPriceRejectedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionPriceRejectedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("inspection.price.rejected 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }
        rejectPriceUseCase.reject(new RejectPriceCommand(event.productId()));
        log.info("가격 거절 처리 완료 (반송 예정): productId={}", event.productId());
    }

    record InspectionPriceRejectedEvent(UUID productId, UUID sellerId, String reason) {}
}
