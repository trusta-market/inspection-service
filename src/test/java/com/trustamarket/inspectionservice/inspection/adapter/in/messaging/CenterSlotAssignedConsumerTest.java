package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RequestInspectionUseCase;
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
class CenterSlotAssignedConsumerTest {

    @Mock
    private RequestInspectionUseCase requestInspectionUseCase;

    private CenterSlotAssignedConsumer consumer;

    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");
    private static final UUID SELLER_ID  = UUID.fromString("b2c3d4e5-0000-0000-0000-000000000002");
    private static final UUID CENTER_ID  = UUID.fromString("c3d4e5f6-0000-0000-0000-000000000003");

    private static final String VALID_PAYLOAD = """
            {
              "productId": "a1b2c3d4-0000-0000-0000-000000000001",
              "sellerId": "b2c3d4e5-0000-0000-0000-000000000002",
              "centerId": "c3d4e5f6-0000-0000-0000-000000000003",
              "originalPriceAmount": 1500000,
              "currency": "KRW"
            }
            """;

    @BeforeEach
    void setUp() {
        consumer = new CenterSlotAssignedConsumer(requestInspectionUseCase, new ObjectMapper());
    }

    @Test
    @DisplayName("유효한 페이로드를 수신하면 RequestInspectionCommand를 담아 request()를 호출한다")
    void consume_validPayload_callsRequest() {
        consumer.consume(VALID_PAYLOAD);

        ArgumentCaptor<RequestInspectionCommand> captor = ArgumentCaptor.forClass(RequestInspectionCommand.class);
        then(requestInspectionUseCase).should().request(captor.capture());
        RequestInspectionCommand cmd = captor.getValue();
        assertThat(cmd.productId()).isEqualTo(PRODUCT_ID);
        assertThat(cmd.sellerId()).isEqualTo(SELLER_ID);
        assertThat(cmd.centerId()).isEqualTo(CENTER_ID);
        assertThat(cmd.originalPriceAmount()).isEqualTo(1_500_000L);
        assertThat(cmd.currency()).isEqualTo("KRW");
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException을 던지고 UseCase를 호출하지 않는다")
    void consume_invalidJson_throwsRuntimeException() {
        assertThatThrownBy(() -> consumer.consume("invalid json"))
                .isInstanceOf(RuntimeException.class);
        then(requestInspectionUseCase).shouldHaveNoInteractions();
    }
}
