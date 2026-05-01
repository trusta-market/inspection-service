package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteReturnCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.CompleteReturnUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarrierReturnCompletedConsumer {

    private final CompleteReturnUseCase completeReturnUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "carrier.return_completed", groupId = "inspection-service")
    public void consume(String payload) {
        CarrierReturnCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, CarrierReturnCompletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("carrier.return_completed 이벤트 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        completeReturnUseCase.completeReturn(new CompleteReturnCommand(event.productId()));
        log.info("반송 완료 처리: productId={}", event.productId());
    }
}
