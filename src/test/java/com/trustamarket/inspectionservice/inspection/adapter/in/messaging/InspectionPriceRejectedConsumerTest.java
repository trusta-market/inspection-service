package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RejectPriceCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RejectPriceUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.application.service.InboxMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InspectionPriceRejectedConsumerTest {

    @Mock
    private RejectPriceUseCase rejectPriceUseCase;
    @Mock
    private InboxMessageHandler inboxMessageHandler;
    @Mock
    private Acknowledgment ack;

    private InspectionPriceRejectedConsumer consumer;

    private static final String CONSUMER_GROUP = "inspection-service";
    private static final UUID EVENT_ID   = UUID.fromString("e0000000-0000-0000-0000-00000000000b");
    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");

    private static final String VALID_PAYLOAD = """
            {
              "eventId": "e0000000-0000-0000-0000-00000000000b",
              "productId": "a1b2c3d4-0000-0000-0000-000000000001",
              "sellerId": "b2c3d4e5-0000-0000-0000-000000000002",
              "reason": "판매자 가격 거절"
            }
            """;

    @BeforeEach
    void setUp() {
        consumer = new InspectionPriceRejectedConsumer(rejectPriceUseCase, inboxMessageHandler, new ObjectMapper());
    }

    @Test
    @DisplayName("유효 페이로드면 eventId·purpose와 함께 핸들러에 위임하고, 도메인 호출 후 ack한다")
    void consume_validPayload_delegatesAndAcks() {
        given(inboxMessageHandler.process(eq(EVENT_ID), eq(CONSUMER_GROUP), eq(InboxPurpose.INSPECTION_PRICE_REJECTED), any(Runnable.class)))
                .willAnswer(inv -> {
                    inv.getArgument(3, Runnable.class).run();
                    return true;
                });

        consumer.consume(VALID_PAYLOAD, ack);

        ArgumentCaptor<RejectPriceCommand> captor = ArgumentCaptor.forClass(RejectPriceCommand.class);
        then(rejectPriceUseCase).should().reject(captor.capture());
        assertThat(captor.getValue().productId()).isEqualTo(PRODUCT_ID);
        then(ack).should().acknowledge();
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 ack+skip하고 핸들러·UseCase를 건드리지 않는다 (poison-pill 차단)")
    void consume_invalidJson_acksAndSkips() {
        consumer.consume("invalid json", ack);

        then(inboxMessageHandler).shouldHaveNoInteractions();
        then(rejectPriceUseCase).shouldHaveNoInteractions();
        then(ack).should().acknowledge();
    }
}
