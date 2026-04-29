package com.trustamarket.inspectionservice.inspection.application.service;

import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionRepository;
import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InspectionServiceTest {

    @Mock
    private InspectionRepository inspectionRepository;

    @InjectMocks
    private InspectionService inspectionService;

    private static final UUID PRODUCT_ID = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");
    private static final UUID SELLER_ID  = UUID.fromString("b2c3d4e5-0000-0000-0000-000000000002");
    private static final UUID CENTER_ID  = UUID.fromString("c3d4e5f6-0000-0000-0000-000000000003");

    private RequestInspectionCommand validCommand() {
        return new RequestInspectionCommand(PRODUCT_ID, SELLER_ID, CENTER_ID, 1_500_000L, "KRW");
    }

    @Nested
    @DisplayName("검수 요청 (request)")
    class Request {

        @Test
        @DisplayName("유효한 커맨드로 Inspection을 생성하고 저장한다")
        void request_success_savesInspection() {
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.request(validCommand());

            ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);
            then(inspectionRepository).should().save(captor.capture());

            Inspection saved = captor.getValue();
            assertThat(saved.getProductId().value()).isEqualTo(PRODUCT_ID);
            assertThat(saved.getSellerId().value()).isEqualTo(SELLER_ID);
            assertThat(saved.getCenterId().value()).isEqualTo(CENTER_ID);
            assertThat(saved.getOriginalPrice().amount().longValue()).isEqualTo(1_500_000L);
            assertThat(saved.getStatus()).isEqualTo(InspectionStatus.REQUESTED);
        }

        @Test
        @DisplayName("생성된 Inspection의 ID는 매 요청마다 새로 발급된다")
        void request_generatesUniqueId() {
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.request(validCommand());
            inspectionService.request(validCommand());

            ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);
            then(inspectionRepository).should(org.mockito.Mockito.times(2)).save(captor.capture());

            UUID first  = captor.getAllValues().get(0).getId().value();
            UUID second = captor.getAllValues().get(1).getId().value();
            assertThat(first).isNotEqualTo(second);
        }
    }
}
