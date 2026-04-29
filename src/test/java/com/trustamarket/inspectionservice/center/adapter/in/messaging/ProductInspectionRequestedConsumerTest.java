package com.trustamarket.inspectionservice.center.adapter.in.messaging;

import com.trustamarket.inspectionservice.center.application.dto.command.AssignCenterCommand;
import com.trustamarket.inspectionservice.center.application.port.in.AssignCenterForInspectionUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.timeout;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"product.inspection-requested", "center.slot-assigned"},
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

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private AssignCenterForInspectionUseCase assignCenterForInspectionUseCase;

    @BeforeEach
    void waitForConsumerAssignment() throws Exception {
        for (var container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    private static final String VALID_PAYLOAD = """
            {
              "productId": "a1b2c3d4-0000-0000-0000-000000000001",
              "sellerId": "b2c3d4e5-0000-0000-0000-000000000002",
              "originalPrice": 1500000,
              "currency": "KRW"
            }
            """;

    @Test
    @DisplayName("유효한 페이로드를 수신하면 assign()을 호출한다")
    void consume_validPayload_callsAssign() throws Exception {
        kafkaTemplate.send("product.inspection-requested", VALID_PAYLOAD).get();

        then(assignCenterForInspectionUseCase).should(timeout(5000)).assign(any(AssignCenterCommand.class));
    }
}
