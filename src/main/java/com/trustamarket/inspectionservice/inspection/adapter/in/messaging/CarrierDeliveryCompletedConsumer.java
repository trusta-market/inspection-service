package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.MarkArrivedUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarrierDeliveryCompletedConsumer {

    private final MarkArrivedUseCase markArrivedUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "carrier.delivery_completed", groupId = "inspection-service")
    public void consume(String payload, Acknowledgment ack) {
        CarrierDeliveryCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, CarrierDeliveryCompletedEvent.class);
        } catch (JsonProcessingException e) {
            // 잘못된 payload는 재시도해도 실패 → ack로 건너뛰어 무한 재소비(poison-pill) 차단
            log.error("carrier.delivery_completed 이벤트 역직렬화 실패 — skip: payload={}", payload, e);
            ack.acknowledge();
            return;
        }

        markArrivedUseCase.markArrived(new MarkArrivedCommand(event.productId()));
        log.info("상품 도착 확인 완료: productId={}", event.productId());
        // 처리 성공 후 오프셋 커밋 — manual ack 모드에서 ack 누락 시 미커밋→재기동 시 토픽 전체 재소비(#61)
        ack.acknowledge();
    }
}
