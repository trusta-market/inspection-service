package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.AcceptPriceCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.AcceptPriceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionPriceAcceptedConsumer {

    private final AcceptPriceUseCase acceptPriceUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.price.accepted", groupId = "inspection-service")
    public void consume(String payload) {
        InspectionPriceAcceptedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionPriceAcceptedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("inspection.price.accepted 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }
        acceptPriceUseCase.accept(new AcceptPriceCommand(event.productId()));
        log.info("가격 수락 처리 완료: productId={}", event.productId());
    }

    record InspectionPriceAcceptedEvent(UUID productId, UUID sellerId, Long finalPrice) {}
}
