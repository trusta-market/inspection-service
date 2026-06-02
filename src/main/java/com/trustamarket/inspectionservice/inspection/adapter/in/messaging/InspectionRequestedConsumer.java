package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RequestInspectionUseCase;
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
public class InspectionRequestedConsumer {

    private static final String CONSUMER_GROUP = "inspection-service";

    private final RequestInspectionUseCase requestInspectionUseCase;
    private final InboxMessageHandler inboxMessageHandler;
    private final ObjectMapper objectMapper;

    // dedup+도메인은 InboxMessageHandler가 한 트랜잭션으로 처리. 반환 시점엔 커밋 완료 → 동기 ack 안전(유실 0).
    @KafkaListener(topics = "inspection.requested", groupId = CONSUMER_GROUP)
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

        boolean processed = inboxMessageHandler.process(
                event.eventId(), CONSUMER_GROUP, InboxPurpose.INSPECTION_REQUESTED,
                () -> requestInspectionUseCase.request(new RequestInspectionCommand(
                        event.productId(), event.sellerId(), event.centerId(),
                        event.originalPriceAmount(), event.currency())));

        if (processed) {
            log.info("검수 요청 생성 완료: eventId={}, productId={}, centerId={}", event.eventId(), event.productId(), event.centerId());
        } else {
            log.info("inspection.requested 중복 메시지 — skip: eventId={}, productId={}", event.eventId(), event.productId());
        }
        ack.acknowledge();
    }
}
