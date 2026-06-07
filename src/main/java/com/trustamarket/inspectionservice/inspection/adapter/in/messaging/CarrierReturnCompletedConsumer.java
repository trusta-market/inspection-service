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

    @KafkaListener(topics = "carrier.return_completed", groupId = CONSUMER_GROUP)
    public void consume(String payload, Acknowledgment ack) {
        CarrierReturnCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, CarrierReturnCompletedEvent.class);
        } catch (JsonProcessingException e) {
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
