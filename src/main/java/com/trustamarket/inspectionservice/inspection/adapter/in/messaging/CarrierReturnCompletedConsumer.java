package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteReturnCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.CompleteReturnUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.application.service.InboxMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarrierReturnCompletedConsumer {

    private static final String CONSUMER_GROUP = "inspection-service";

    private final CompleteReturnUseCase completeReturnUseCase;
    private final InboxMessageHandler inboxMessageHandler;
    private final ObjectMapper objectMapper;

    // dedup+도메인은 InboxMessageHandler가 한 트랜잭션으로 처리. 반환 시점엔 커밋 완료 → 동기 ack 안전(유실 0).
    @KafkaListener(topics = "carrier.return_completed", groupId = CONSUMER_GROUP)
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

        boolean processed = inboxMessageHandler.process(
                event.eventId(), CONSUMER_GROUP, InboxPurpose.CARRIER_RETURN_COMPLETED,
                () -> completeReturnUseCase.completeReturn(new CompleteReturnCommand(event.productId())));

        if (processed) {
            log.info("반송 완료 처리: eventId={}, productId={}", event.eventId(), event.productId());
        } else {
            log.info("carrier.return_completed 중복 메시지 — skip: eventId={}, productId={}", event.eventId(), event.productId());
        }
        ack.acknowledge();
    }
}
