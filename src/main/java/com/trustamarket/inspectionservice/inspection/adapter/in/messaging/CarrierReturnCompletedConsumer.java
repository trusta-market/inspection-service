package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteReturnCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.CompleteReturnUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarrierReturnCompletedConsumer {

    private final CompleteReturnUseCase completeReturnUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "carrier.return_completed", groupId = "inspection-service")
    public void consume(String payload, Acknowledgment ack) {
        CarrierReturnCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, CarrierReturnCompletedEvent.class);
        } catch (JsonProcessingException e) {
            // 잘못된 payload는 재시도해도 실패 → ack로 건너뛰어 무한 재소비(poison-pill) 차단
            log.error("carrier.return_completed 이벤트 역직렬화 실패 — skip: payload={}", payload, e);
            ack.acknowledge();
            return;
        }

        completeReturnUseCase.completeReturn(new CompleteReturnCommand(event.productId()));
        log.info("반송 완료 처리: productId={}", event.productId());
        // 처리 성공 후 오프셋 커밋 — manual ack 모드에서 ack 누락 시 미커밋→재기동 시 토픽 전체 재소비(#61)
        ack.acknowledge();
    }
}
