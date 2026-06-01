package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.AcceptPriceCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.AcceptPriceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionPriceAcceptedConsumer {

    private final AcceptPriceUseCase acceptPriceUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.price.accepted", groupId = "inspection-service")
    public void consume(String payload, Acknowledgment ack) {
        InspectionPriceAcceptedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionPriceAcceptedEvent.class);
        } catch (JsonProcessingException e) {
            // 잘못된 payload는 재시도해도 실패 → ack로 건너뛰어 무한 재소비(poison-pill) 차단
            log.error("inspection.price.accepted 역직렬화 실패 — skip: payload={}", payload, e);
            ack.acknowledge();
            return;
        }
        acceptPriceUseCase.accept(new AcceptPriceCommand(event.productId()));
        log.info("가격 수락 처리 완료: productId={}", event.productId());
        // 처리 성공 후 오프셋 커밋 — manual ack 모드에서 ack 누락 시 미커밋→재기동 시 토픽 전체 재소비(#61)
        ack.acknowledge();
    }

    record InspectionPriceAcceptedEvent(UUID productId, UUID sellerId, Long finalPrice) {}
}
