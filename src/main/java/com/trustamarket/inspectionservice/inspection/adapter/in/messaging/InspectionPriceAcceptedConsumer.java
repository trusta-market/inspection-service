package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.AcceptPriceCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.AcceptPriceUseCase;
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
public class InspectionPriceAcceptedConsumer {

    private static final String CONSUMER_GROUP = "inspection-service";

    private final AcceptPriceUseCase acceptPriceUseCase;
    private final InboxMessageHandler inboxMessageHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.price.accepted", groupId = CONSUMER_GROUP)
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

        boolean processed = inboxMessageHandler.process(
                event.eventId(), CONSUMER_GROUP, InboxPurpose.INSPECTION_PRICE_ACCEPTED,
                () -> acceptPriceUseCase.accept(new AcceptPriceCommand(event.productId())));

        if (processed) {
            log.info("가격 수락 처리 완료: eventId={}, productId={}", event.eventId(), event.productId());
        } else {
            log.info("inspection.price.accepted 중복 메시지 — skip: eventId={}, productId={}", event.eventId(), event.productId());
        }
        ack.acknowledge();
    }
}
