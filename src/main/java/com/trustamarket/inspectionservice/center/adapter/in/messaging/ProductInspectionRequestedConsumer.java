package com.trustamarket.inspectionservice.center.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.center.application.dto.command.AssignCenterCommand;
import com.trustamarket.inspectionservice.center.application.port.in.AssignCenterForInspectionUseCase;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductInspectionRequestedConsumer {

    private final AssignCenterForInspectionUseCase assignCenterForInspectionUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product.inspection-requested", groupId = "inspection-service")
    public void consume(String payload) {
        ProductInspectionRequestedEvent event;
        try {
            event = objectMapper.readValue(payload, ProductInspectionRequestedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("product.inspection-requested 이벤트 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        try {
            assignCenterForInspectionUseCase.assign(new AssignCenterCommand(
                    event.productId(), event.sellerId(), event.originalPrice(), event.currency()
            ));
        } catch (InspectionCenterException e) {
            log.error("검수 센터 슬롯 예약 실패 — 가용 센터 없음: productId={}", event.productId(), e);
        }
    }
}
