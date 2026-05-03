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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CarrierDeliveryCompletedConsumerTest {

    @Mock
    private MarkArrivedUseCase markArrivedUseCase;

    private CarrierDeliveryCompletedConsumer consumer;

    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        consumer = new CarrierDeliveryCompletedConsumer(markArrivedUseCase, new ObjectMapper());
    }

    @Test
    @DisplayName("유효한 페이로드를 수신하면 productId를 담은 커맨드로 markArrived()를 호출한다")
    void consume_validPayload_callsMarkArrived() {
        String payload = """
                {"productId": "a1b2c3d4-0000-0000-0000-000000000001"}
                """;

        consumer.consume(payload);

        ArgumentCaptor<MarkArrivedCommand> captor = ArgumentCaptor.forClass(MarkArrivedCommand.class);
        then(markArrivedUseCase).should().markArrived(captor.capture());
        assertThat(captor.getValue().productId()).isEqualTo(PRODUCT_ID);
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException을 던지고 UseCase를 호출하지 않는다")
    void consume_invalidJson_throwsRuntimeException() {
        assertThatThrownBy(() -> consumer.consume("invalid json"))
                .isInstanceOf(RuntimeException.class);
        then(markArrivedUseCase).shouldHaveNoInteractions();
    }
}
