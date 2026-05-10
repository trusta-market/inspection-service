package com.trustamarket.inspectionservice.center.adapter.in.messaging;

import com.trustamarket.inspectionservice.center.application.dto.result.ReserveSlotResult;
import com.trustamarket.inspectionservice.center.application.port.in.ReserveSlotUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.timeout;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = "product.inspection-requested",
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema="
})
@DirtiesContext
class ProductInspectionRequestedConsumerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // 컨슈머가 파티션에 할당될 때까지 대기하기 위해 사용
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    // 컨슈머가 메시지를 받아 use case를 호출하는지만 검증
    @MockitoBean
    private ReserveSlotUseCase reserveSlotUseCase;

    @BeforeEach
    void waitForConsumerAssignment() throws Exception {
        // 모든 리스너 컨테이너가 파티션 할당을 완료할 때까지 대기
        for (var container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    private static final String VALID_PAYLOAD = """
            {
              "productId": "a1b2c3d4-0000-0000-0000-000000000001",
              "sellerId": "b2c3d4e5-0000-0000-0000-000000000002",
              "originalPrice": 1500000,
              "currency": "KRW",
              "inspectionType": "STANDARD"
            }
            """;

    @Test
    @DisplayName("유효한 페이로드를 수신하면 reserveSlot()을 호출한다")
    void consume_validPayload_callsReserveSlot() throws Exception {
        given(reserveSlotUseCase.reserveSlot())
                .willReturn(new ReserveSlotResult(UUID.randomUUID()));

        // .get()으로 브로커에 메시지가 실제로 전달될 때까지 블로킹
        kafkaTemplate.send("product.inspection-requested", VALID_PAYLOAD).get();

        // Kafka 소비는 비동기이므로 최대 5초 대기 후 호출 여부 검증
        then(reserveSlotUseCase).should(timeout(5000)).reserveSlot();
    }
}
