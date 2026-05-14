package com.trustamarket.inspectionservice.support;

import com.trustamarket.inspectionservice.inspection.application.port.in.MarkArrivedUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.RequestInspectionUseCase;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"product.inspection-requested", "center.slot-assigned", "delivery.completed"},
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
public abstract class KafkaConsumerIntegrationTest {

    @Autowired
    protected KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    protected KafkaListenerEndpointRegistry registry;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    protected RequestInspectionUseCase requestInspectionUseCase;

    @MockitoBean
    protected MarkArrivedUseCase markArrivedUseCase;

    @BeforeEach
    void waitForConsumerAssignment() throws Exception {
        for (var container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }
}
