package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.MarkArrivedUseCase;
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
public class CarrierDeliveryCompletedConsumer {

    private static final String CONSUMER_GROUP = "inspection-service";

    private final MarkArrivedUseCase markArrivedUseCase;
    private final InboxMessageHandler inboxMessageHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "carrier.delivery_completed", groupId = CONSUMER_GROUP)
    public void consume(String payload, Acknowledgment ack) {
        CarrierDeliveryCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, CarrierDeliveryCompletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("carrier.delivery_completed 이벤트 역직렬화 실패 — skip: payload={}", payload, e);
            ack.acknowledge();
            return;
        }

        boolean processed = inboxMessageHandler.process(
                event.eventId(), CONSUMER_GROUP, InboxPurpose.CARRIER_DELIVERY_COMPLETED,
                () -> markArrivedUseCase.markArrived(new MarkArrivedCommand(event.productId())));

        if (processed) {
            log.info("상품 도착 확인 완료: eventId={}, productId={}", event.eventId(), event.productId());
        } else {
            log.info("carrier.delivery_completed 중복 메시지 — skip: eventId={}, productId={}", event.eventId(), event.productId());
        }
        ack.acknowledge();
    }
}
