package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.MarkArrivedUseCase;
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
class CarrierDeliveryCompletedConsumerTest {

    @Mock
    private MarkArrivedUseCase markArrivedUseCase;
    @Mock
    private InboxMessageHandler inboxMessageHandler;
    @Mock
    private Acknowledgment ack;

    private CarrierDeliveryCompletedConsumer consumer;

    private static final String CONSUMER_GROUP = "inspection-service";
    private static final UUID EVENT_ID   = UUID.fromString("e0000000-0000-0000-0000-000000000009");
    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");

    private static final String VALID_PAYLOAD = """
            {
              "eventId": "e0000000-0000-0000-0000-000000000009",
              "productId": "a1b2c3d4-0000-0000-0000-000000000001"
            }
            """;

    @BeforeEach
    void setUp() {
        consumer = new CarrierDeliveryCompletedConsumer(markArrivedUseCase, inboxMessageHandler, new ObjectMapper());
    }

    @Test
    @DisplayName("유효 페이로드면 eventId·purpose와 함께 핸들러에 위임하고, 도메인 호출 후 ack한다")
    void consume_validPayload_delegatesAndAcks() {
        // 핸들러가 처음 보는 이벤트로 판단(true)하고 domainWork를 실행하는 상황을 흉내낸다.
        given(inboxMessageHandler.process(eq(EVENT_ID), eq(CONSUMER_GROUP), eq(InboxPurpose.CARRIER_DELIVERY_COMPLETED), any(Runnable.class)))
                .willAnswer(inv -> {
                    inv.getArgument(3, Runnable.class).run();
                    return true;
                });

        consumer.consume(VALID_PAYLOAD, ack);

        ArgumentCaptor<MarkArrivedCommand> captor = ArgumentCaptor.forClass(MarkArrivedCommand.class);
        then(markArrivedUseCase).should().markArrived(captor.capture());
        assertThat(captor.getValue().productId()).isEqualTo(PRODUCT_ID);
        then(ack).should().acknowledge();
    }

    @Test
    @DisplayName("중복 이벤트(핸들러 false)면 도메인을 호출하지 않고 ack만 한다 (at-least-once 재배달 멱등)")
    void consume_duplicate_skipsDomainButAcks() {
        given(inboxMessageHandler.process(eq(EVENT_ID), eq(CONSUMER_GROUP), eq(InboxPurpose.CARRIER_DELIVERY_COMPLETED), any(Runnable.class)))
                .willReturn(false);

        consumer.consume(VALID_PAYLOAD, ack);

        then(markArrivedUseCase).shouldHaveNoInteractions();
        then(ack).should().acknowledge();
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 ack+skip하고 핸들러·UseCase를 건드리지 않는다 (poison-pill 차단)")
    void consume_invalidJson_acksAndSkips() {
        consumer.consume("invalid json", ack);

        then(inboxMessageHandler).shouldHaveNoInteractions();
        then(markArrivedUseCase).shouldHaveNoInteractions();
        then(ack).should().acknowledge();
    }
}
