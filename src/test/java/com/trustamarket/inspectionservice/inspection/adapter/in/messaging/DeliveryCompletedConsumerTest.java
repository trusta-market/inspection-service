package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.support.KafkaConsumerIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.timeout;

class DeliveryCompletedConsumerTest extends KafkaConsumerIntegrationTest {

    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");

    private static final String VALID_PAYLOAD = """
            {
                "productId": "a1b2c3d4-0000-0000-0000-000000000001"
            }
            """;

    @Test
    @DisplayName("유효한 페이로드를 수신하면 productId를 담은 커맨드로 markArrived()를 호출한다")
    void consume_validPayload_callsMarkArrived() throws Exception {
        kafkaTemplate.send("delivery.completed", VALID_PAYLOAD).get();

        ArgumentCaptor<MarkArrivedCommand> captor = ArgumentCaptor.forClass(MarkArrivedCommand.class);
        then(markArrivedUseCase).should(timeout(5000)).markArrived(captor.capture());
        assertThat(captor.getValue().productId()).isEqualTo(PRODUCT_ID);
    }
}
