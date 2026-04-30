package com.trustamarket.inspectionservice.inspection.adapter.in.messaging;

import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.support.KafkaConsumerIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.timeout;

class CenterSlotAssignedConsumerTest extends KafkaConsumerIntegrationTest {

    private static final String VALID_PAYLOAD = """
            {
              "productId": "a1b2c3d4-0000-0000-0000-000000000001",
              "sellerId": "b2c3d4e5-0000-0000-0000-000000000002",
              "centerId": "c3d4e5f6-0000-0000-0000-000000000003",
              "originalPriceAmount": 1500000,
              "currency": "KRW"
            }
            """;

    @Test
    @DisplayName("유효한 페이로드를 수신하면 request()를 호출한다")
    void consume_validPayload_callsRequest() throws Exception {
        kafkaTemplate.send("center.slot-assigned", VALID_PAYLOAD).get();

        then(requestInspectionUseCase).should(timeout(5000)).request(any(RequestInspectionCommand.class));
    }
}
