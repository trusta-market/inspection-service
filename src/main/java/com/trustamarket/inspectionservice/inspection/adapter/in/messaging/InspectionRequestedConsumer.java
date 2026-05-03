package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RequestInspectionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionRequestedConsumer {

    private final RequestInspectionUseCase requestInspectionUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.requested", groupId = "inspection-service")
    public void consume(String payload) {
        InspectionRequestedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionRequestedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("inspection.requested 이벤트 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        requestInspectionUseCase.request(new RequestInspectionCommand(
                event.productId(), event.sellerId(), event.centerId(),
                event.originalPriceAmount(), event.currency()
        ));
        log.info("검수 요청 생성 완료: productId={}, centerId={}", event.productId(), event.centerId());
    }
}
