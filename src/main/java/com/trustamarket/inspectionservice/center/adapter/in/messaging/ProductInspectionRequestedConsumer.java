package com.trustamarket.inspectionservice.center.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.center.application.dto.result.ReserveSlotResult;
import com.trustamarket.inspectionservice.center.application.port.in.ReserveSlotUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductInspectionRequestedConsumer {

    private final ReserveSlotUseCase reserveSlotUseCase;
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

        ReserveSlotResult result = reserveSlotUseCase.reserveSlot();
        log.info("슬롯 예약 완료: centerId={}, productId={}", result.centerId(), event.productId());
    }
}
