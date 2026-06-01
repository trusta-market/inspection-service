package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RequestInspectionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionRequestedConsumer {

    private final RequestInspectionUseCase requestInspectionUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.requested", groupId = "inspection-service")
    public void consume(String payload, Acknowledgment ack) {
        InspectionRequestedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionRequestedEvent.class);
        } catch (JsonProcessingException e) {
            // 잘못된 payload는 재시도해도 실패 → ack로 건너뛰어 무한 재소비(poison-pill) 차단
            log.error("inspection.requested 이벤트 역직렬화 실패 — skip: payload={}", payload, e);
            ack.acknowledge();
            return;
        }

        requestInspectionUseCase.request(new RequestInspectionCommand(
                event.productId(), event.sellerId(), event.centerId(),
                event.originalPriceAmount(), event.currency()
        ));
        log.info("검수 요청 생성 완료: productId={}, centerId={}", event.productId(), event.centerId());
        // 처리 성공 후 오프셋 커밋 — manual ack 모드에서 ack 누락 시 미커밋→재기동 시 토픽 전체 재소비(#61)
        ack.acknowledge();
    }
}
