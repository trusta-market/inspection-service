package com.trustamarket.inspectionservice.center.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.center.application.dto.command.AssignCenterCommand;
import com.trustamarket.inspectionservice.center.application.port.in.AssignCenterForInspectionUseCase;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class ProductInspectionRequestedConsumerTest {

    @Mock
    private AssignCenterForInspectionUseCase assignCenterForInspectionUseCase;

    private ProductInspectionRequestedConsumer consumer;

    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");
    private static final UUID SELLER_ID  = UUID.fromString("b2c3d4e5-0000-0000-0000-000000000002");

    private static final String VALID_PAYLOAD = """
            {
              "productId": "a1b2c3d4-0000-0000-0000-000000000001",
              "sellerId": "b2c3d4e5-0000-0000-0000-000000000002",
              "originalPrice": 1500000,
              "currency": "KRW"
            }
            """;

    @BeforeEach
    void setUp() {
        consumer = new ProductInspectionRequestedConsumer(assignCenterForInspectionUseCase, new ObjectMapper());
    }

    @Test
    @DisplayName("유효한 페이로드를 수신하면 AssignCenterCommand를 담아 assign()을 호출한다")
    void consume_validPayload_callsAssign() {
        consumer.consume(VALID_PAYLOAD);

        ArgumentCaptor<AssignCenterCommand> captor = ArgumentCaptor.forClass(AssignCenterCommand.class);
        then(assignCenterForInspectionUseCase).should().assign(captor.capture());
        AssignCenterCommand cmd = captor.getValue();
        assertThat(cmd.productId()).isEqualTo(PRODUCT_ID);
        assertThat(cmd.sellerId()).isEqualTo(SELLER_ID);
        assertThat(cmd.originalPriceAmount()).isEqualTo(1_500_000L);
        assertThat(cmd.currency()).isEqualTo("KRW");
    }

    @Test
    @DisplayName("가용 센터가 없어 InspectionCenterException이 발생해도 예외를 전파하지 않는다")
    void consume_noCenterAvailable_doesNotPropagate() {
        willThrow(InspectionCenterException.class).given(assignCenterForInspectionUseCase).assign(any());

        assertThatNoException().isThrownBy(() -> consumer.consume(VALID_PAYLOAD));
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException을 던지고 UseCase를 호출하지 않는다")
    void consume_invalidJson_throwsRuntimeException() {
        assertThatThrownBy(() -> consumer.consume("invalid json"))
                .isInstanceOf(RuntimeException.class);
        then(assignCenterForInspectionUseCase).shouldHaveNoInteractions();
    }
}
