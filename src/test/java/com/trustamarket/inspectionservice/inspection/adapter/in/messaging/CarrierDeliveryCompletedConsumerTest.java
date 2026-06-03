package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.MarkArrivedUseCase;
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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CarrierDeliveryCompletedConsumerTest {

    @Mock
    private MarkArrivedUseCase markArrivedUseCase;
    @Mock
    private Acknowledgment ack;

    private CarrierDeliveryCompletedConsumer consumer;

    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        consumer = new CarrierDeliveryCompletedConsumer(markArrivedUseCase, new ObjectMapper());
    }

    @Test
    @DisplayName("유효한 페이로드를 수신하면 markArrived() 호출 후 ack한다")
    void consume_validPayload_callsMarkArrivedAndAcks() {
        String payload = """
                {"productId": "a1b2c3d4-0000-0000-0000-000000000001"}
                """;

        consumer.consume(payload, ack);

        ArgumentCaptor<MarkArrivedCommand> captor = ArgumentCaptor.forClass(MarkArrivedCommand.class);
        then(markArrivedUseCase).should().markArrived(captor.capture());
        assertThat(captor.getValue().productId()).isEqualTo(PRODUCT_ID);
        then(ack).should().acknowledge();
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 ack+skip하고 UseCase를 호출하지 않는다 (poison-pill 차단)")
    void consume_invalidJson_acksAndSkips() {
        consumer.consume("invalid json", ack);

        then(markArrivedUseCase).shouldHaveNoInteractions();
        then(ack).should().acknowledge();
    }
}
