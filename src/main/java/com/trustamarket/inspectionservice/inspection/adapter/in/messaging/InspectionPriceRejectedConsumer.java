package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RejectPriceCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RejectPriceUseCase;
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
public class InspectionPriceRejectedConsumer {

    private static final String CONSUMER_GROUP = "inspection-service";

    private final RejectPriceUseCase rejectPriceUseCase;
    private final InboxMessageHandler inboxMessageHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.price.rejected", groupId = CONSUMER_GROUP)
    public void consume(String payload, Acknowledgment ack) {
        InspectionPriceRejectedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionPriceRejectedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("inspection.price.rejected 역직렬화 실패 — skip: payload={}", payload, e);
            ack.acknowledge();
            return;
        }

        boolean processed = inboxMessageHandler.process(
                event.eventId(), CONSUMER_GROUP, InboxPurpose.INSPECTION_PRICE_REJECTED,
                () -> rejectPriceUseCase.reject(new RejectPriceCommand(event.productId())));

        if (processed) {
            log.info("가격 거절 처리 완료 (반송 예정): eventId={}, productId={}", event.eventId(), event.productId());
        } else {
            log.info("inspection.price.rejected 중복 메시지 — skip: eventId={}, productId={}", event.eventId(), event.productId());
        }
        ack.acknowledge();
    }
}
