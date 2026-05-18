package com.trustamarket.inspectionservice.inspection.application.service;

import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteReturnCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.FailInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionFailedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionReturnCompletedEvent;
import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.StartInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.query.GetMyInspectionsQuery;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionPageResult;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionResult;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionCompletedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionStartedEvent;
import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionEventPublisher;
import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionRepository;
import com.trustamarket.inspectionservice.inspection.domain.enums.CurrencyCode;
import com.trustamarket.inspectionservice.inspection.domain.enums.Grade;
import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectorId;
import com.trustamarket.inspectionservice.inspection.domain.vo.Money;
import com.trustamarket.inspectionservice.inspection.domain.vo.ProductId;
import com.trustamarket.inspectionservice.inspection.domain.vo.SellerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InspectionServiceTest {

    @Mock
    private InspectionRepository inspectionRepository;

    @Mock
    private InspectionEventPublisher inspectionEventPublisher;

    @InjectMocks
    private InspectionService inspectionService;

    private static final UUID PRODUCT_ID   = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");
    private static final UUID SELLER_ID    = UUID.fromString("b2c3d4e5-0000-0000-0000-000000000002");
    private static final UUID CENTER_ID    = UUID.fromString("c3d4e5f6-0000-0000-0000-000000000003");
    private static final UUID INSPECTOR_ID = UUID.fromString("d4e5f6a7-0000-0000-0000-000000000004");

    private RequestInspectionCommand validCommand() {
        return new RequestInspectionCommand(PRODUCT_ID, SELLER_ID, CENTER_ID, 1_500_000L, "KRW");
    }

    private Inspection requestedInspection() {
        return Inspection.request(
                InspectionId.generate(),
                ProductId.of(PRODUCT_ID),
                SellerId.of(SELLER_ID),
                CenterId.of(CENTER_ID),
                Money.of(1_500_000L, CurrencyCode.KRW),
                Instant.now()
        );
    }

    private Inspection arrivedInspection() {
        Inspection inspection = requestedInspection();
        inspection.markArrived(Instant.now());
        return inspection;
    }

    private Inspection inProgressInspection() {
        Inspection inspection = arrivedInspection();
        inspection.start(InspectorId.of(INSPECTOR_ID), Instant.now());
        return inspection;
    }

    private Inspection failedInspection() {
        Inspection inspection = inProgressInspection();
        inspection.failInspection("심각한 손상 발견", null, Instant.now());
        return inspection;
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

    @Nested
    @DisplayName("도착 확인 (markArrived)")
    class MarkArrived {

        @Test
        @DisplayName("REQUESTED 상태의 검수 요청을 ARRIVED로 전이하고 저장한다")
        void markArrived_success() {
            Inspection inspection = requestedInspection();
            given(inspectionRepository.findByProductId(ProductId.of(PRODUCT_ID))).willReturn(Optional.of(inspection));
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.markArrived(new MarkArrivedCommand(PRODUCT_ID));

            ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);
            then(inspectionRepository).should().save(captor.capture());
            Inspection saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(InspectionStatus.ARRIVED);
            assertThat(saved.getArrivedAt()).isNotNull();
        }

        @Test
        @DisplayName("productId에 해당하는 검수 요청이 없으면 InspectionException을 던진다")
        void markArrived_notFound_throwsException() {
            given(inspectionRepository.findByProductId(ProductId.of(PRODUCT_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionService.markArrived(new MarkArrivedCommand(PRODUCT_ID)))
                    .isInstanceOf(InspectionException.class)
                    .hasMessageContaining(PRODUCT_ID.toString());
        }
    }

    @Nested
    @DisplayName("검수 시작 (start)")
    class Start {

        @Test
        @DisplayName("ARRIVED 상태의 검수 요청을 IN_PROGRESS로 전이하고 이벤트를 발행한다")
        void start_success() {
            Inspection inspection = arrivedInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.start(new StartInspectionCommand(inspection.getId().value(), INSPECTOR_ID));

            ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);
            then(inspectionRepository).should().save(captor.capture());
            Inspection saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(InspectionStatus.IN_PROGRESS);
            assertThat(saved.getInspectorId().value()).isEqualTo(INSPECTOR_ID);
            assertThat(saved.getStartedAt()).isNotNull();
            then(inspectionEventPublisher).should().publish(any(InspectionStartedEvent.class));
        }

        @Test
        @DisplayName("inspectionId에 해당하는 검수 요청이 없으면 InspectionException을 던진다")
        void start_notFound_throwsException() {
            UUID unknownId = UUID.randomUUID();
            given(inspectionRepository.findById(InspectionId.of(unknownId))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionService.start(new StartInspectionCommand(unknownId, INSPECTOR_ID)))
                    .isInstanceOf(InspectionException.class)
                    .hasMessageContaining(unknownId.toString());
        }

        @Test
        @DisplayName("ARRIVED 상태가 아니면 InspectionException을 던진다")
        void start_wrongStatus_throwsException() {
            Inspection inspection = requestedInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));

            assertThatThrownBy(() -> inspectionService.start(new StartInspectionCommand(inspection.getId().value(), INSPECTOR_ID)))
                    .isInstanceOf(InspectionException.class);
        }
    }

    @Nested
    @DisplayName("검수 완료 (complete)")
    class Complete {

        private CompleteInspectionCommand validCommand(UUID inspectionId) {
            return new CompleteInspectionCommand(inspectionId, "A", 1_200_000L, "KRW", "상태 양호", null);
        }

        @Test
        @DisplayName("IN_PROGRESS 상태의 검수를 PRICED로 전이하고 이벤트 1개를 발행한다")
        void complete_success() {
            Inspection inspection = inProgressInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.complete(validCommand(inspection.getId().value()));

            ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);
            then(inspectionRepository).should().save(captor.capture());
            Inspection saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(InspectionStatus.PRICED);
            assertThat(saved.getGrade()).isEqualTo(Grade.A);
            assertThat(saved.getSuggestedPrice().amount().longValue()).isEqualTo(1_200_000L);
            assertThat(saved.getInspectorNote()).isEqualTo("상태 양호");
            assertThat(saved.getPricedAt()).isNotNull();

            then(inspectionEventPublisher).should().publish(any(InspectionCompletedEvent.class));
        }

        @Test
        @DisplayName("InspectionCompletedEvent에 grade, suggestedPriceAmount, currency, inspectorId가 포함된다")
        void complete_event_containsPriceInfo() {
            Inspection inspection = inProgressInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.complete(validCommand(inspection.getId().value()));

            ArgumentCaptor<InspectionCompletedEvent> captor = ArgumentCaptor.forClass(InspectionCompletedEvent.class);
            then(inspectionEventPublisher).should().publish(captor.capture());
            InspectionCompletedEvent event = captor.getValue();
            assertThat(event.grade()).isEqualTo("A");
            assertThat(event.suggestedPriceAmount()).isEqualTo(1_200_000L);
            assertThat(event.currency()).isEqualTo("KRW");
            assertThat(event.inspectorId()).isEqualTo(INSPECTOR_ID);
        }

        @Test
        @DisplayName("inspectionId에 해당하는 검수 요청이 없으면 InspectionException을 던진다")
        void complete_notFound_throwsException() {
            UUID unknownId = UUID.randomUUID();
            given(inspectionRepository.findById(InspectionId.of(unknownId))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionService.complete(validCommand(unknownId)))
                    .isInstanceOf(InspectionException.class)
                    .hasMessageContaining(unknownId.toString());
        }

        @Test
        @DisplayName("IN_PROGRESS 상태가 아니면 InspectionException을 던진다")
        void complete_wrongStatus_throwsException() {
            Inspection inspection = arrivedInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));

            assertThatThrownBy(() -> inspectionService.complete(validCommand(inspection.getId().value())))
                    .isInstanceOf(InspectionException.class);
        }
    }

    @Nested
    @DisplayName("검수 실패 (fail)")
    class Fail {

        @Test
        @DisplayName("IN_PROGRESS 상태의 검수를 FAILED로 전이하고 저장한다")
        void fail_success() {
            Inspection inspection = inProgressInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.fail(new FailInspectionCommand(inspection.getId().value(), "심각한 손상 발견", null));

            ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);
            then(inspectionRepository).should().save(captor.capture());
            Inspection saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(InspectionStatus.FAILED);
            assertThat(saved.getInspectorNote()).isEqualTo("심각한 손상 발견");
            assertThat(saved.getInspectionDoneAt()).isNotNull();
            then(inspectionEventPublisher).should().publish(any(InspectionFailedEvent.class));
        }

        @Test
        @DisplayName("inspectionId에 해당하는 검수 요청이 없으면 InspectionException을 던진다")
        void fail_notFound_throwsException() {
            UUID unknownId = UUID.randomUUID();
            given(inspectionRepository.findById(InspectionId.of(unknownId))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionService.fail(new FailInspectionCommand(unknownId, "손상", null)))
                    .isInstanceOf(InspectionException.class)
                    .hasMessageContaining(unknownId.toString());
        }

        @Test
        @DisplayName("IN_PROGRESS 상태가 아니면 InspectionException을 던진다")
        void fail_wrongStatus_throwsException() {
            Inspection inspection = arrivedInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));

            assertThatThrownBy(() -> inspectionService.fail(new FailInspectionCommand(inspection.getId().value(), "손상", null)))
                    .isInstanceOf(InspectionException.class);
        }

        @Test
        @DisplayName("inspectorNote가 blank이면 InspectionException을 던진다")
        void fail_blankNote_throwsException() {
            Inspection inspection = inProgressInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));

            assertThatThrownBy(() -> inspectionService.fail(new FailInspectionCommand(inspection.getId().value(), "  ", null)))
                    .isInstanceOf(InspectionException.class);
        }
    }

    @Nested
    @DisplayName("반송 완료 (completeReturn)")
    class CompleteReturn {

        @Test
        @DisplayName("FAILED 상태의 검수를 RETURN_COMPLETED로 전이하고 이벤트를 발행한다")
        void completeReturn_success() {
            Inspection inspection = failedInspection();
            given(inspectionRepository.findByProductId(ProductId.of(PRODUCT_ID))).willReturn(Optional.of(inspection));
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.completeReturn(new CompleteReturnCommand(PRODUCT_ID));

            ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);
            then(inspectionRepository).should().save(captor.capture());
            Inspection saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(InspectionStatus.RETURN_COMPLETED);
            assertThat(saved.getReturnCompletedAt()).isNotNull();
            then(inspectionEventPublisher).should().publish(any(InspectionReturnCompletedEvent.class));
        }

        @Test
        @DisplayName("InspectionReturnCompletedEvent에 inspectionId, productId, sellerId가 포함된다")
        void completeReturn_event_containsIds() {
            Inspection inspection = failedInspection();
            given(inspectionRepository.findByProductId(ProductId.of(PRODUCT_ID))).willReturn(Optional.of(inspection));
            given(inspectionRepository.save(any(Inspection.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionService.completeReturn(new CompleteReturnCommand(PRODUCT_ID));

            ArgumentCaptor<InspectionReturnCompletedEvent> captor = ArgumentCaptor.forClass(InspectionReturnCompletedEvent.class);
            then(inspectionEventPublisher).should().publish(captor.capture());
            InspectionReturnCompletedEvent event = captor.getValue();
            assertThat(event.productId()).isEqualTo(PRODUCT_ID);
            assertThat(event.sellerId()).isEqualTo(SELLER_ID);
            assertThat(event.inspectionId()).isEqualTo(inspection.getId().value());
        }

        @Test
        @DisplayName("productId에 해당하는 검수 요청이 없으면 InspectionException을 던진다")
        void completeReturn_notFound_throwsException() {
            given(inspectionRepository.findByProductId(ProductId.of(PRODUCT_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionService.completeReturn(new CompleteReturnCommand(PRODUCT_ID)))
                    .isInstanceOf(InspectionException.class)
                    .hasMessageContaining(PRODUCT_ID.toString());
        }

        @Test
        @DisplayName("FAILED 상태가 아니면 InspectionException을 던진다")
        void completeReturn_wrongStatus_throwsException() {
            Inspection inspection = inProgressInspection();
            given(inspectionRepository.findByProductId(ProductId.of(PRODUCT_ID))).willReturn(Optional.of(inspection));

            assertThatThrownBy(() -> inspectionService.completeReturn(new CompleteReturnCommand(PRODUCT_ID)))
                    .isInstanceOf(InspectionException.class);
        }
    }

    @Nested
    @DisplayName("내 검수 목록 조회 (getMyInspections)")
    class GetMyInspections {

        @Test
        @DisplayName("셀러의 검수 목록을 페이징하여 반환한다")
        void getMyInspections_success() {
            Inspection inspection = requestedInspection();
            SellerId sellerId = SellerId.of(SELLER_ID);
            given(inspectionRepository.findBySellerId(eq(sellerId), anyInt(), anyInt()))
                    .willReturn(List.of(inspection));
            given(inspectionRepository.countBySellerId(sellerId)).willReturn(1L);

            GetInspectionPageResult result = inspectionService.getMyInspections(
                    new GetMyInspectionsQuery(SELLER_ID, 0, 10));

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).inspectionId()).isEqualTo(inspection.getId().value());
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("검수 요청이 없으면 빈 목록을 반환한다")
        void getMyInspections_empty() {
            SellerId sellerId = SellerId.of(SELLER_ID);
            given(inspectionRepository.findBySellerId(eq(sellerId), anyInt(), anyInt()))
                    .willReturn(List.of());
            given(inspectionRepository.countBySellerId(sellerId)).willReturn(0L);

            GetInspectionPageResult result = inspectionService.getMyInspections(
                    new GetMyInspectionsQuery(SELLER_ID, 0, 10));

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
            assertThat(result.totalPages()).isZero();
        }
    }

    @Nested
    @DisplayName("검수 상세 조회 (getInspection)")
    class GetInspection {

        @Test
        @DisplayName("판매자 본인이 조회하면 검수 상세 정보를 반환한다")
        void getInspection_success() {
            Inspection inspection = requestedInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));

            GetInspectionResult result = inspectionService.getInspection(inspection.getId().value(), SELLER_ID, false);

            assertThat(result.inspectionId()).isEqualTo(inspection.getId().value());
            assertThat(result.productId()).isEqualTo(PRODUCT_ID);
            assertThat(result.sellerId()).isEqualTo(SELLER_ID);
            assertThat(result.status()).isEqualTo(InspectionStatus.REQUESTED);
        }

        @Test
        @DisplayName("INSPECTOR/ADMIN 권한이면 타인의 검수도 조회할 수 있다")
        void getInspection_privileged_success() {
            Inspection inspection = requestedInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));

            GetInspectionResult result = inspectionService.getInspection(inspection.getId().value(), UUID.randomUUID(), true);

            assertThat(result.inspectionId()).isEqualTo(inspection.getId().value());
        }

        @Test
        @DisplayName("MEMBER가 타인의 검수를 조회하면 INSPECTION_ACCESS_DENIED 예외를 던진다")
        void getInspection_otherMember_throwsAccessDenied() {
            Inspection inspection = requestedInspection();
            given(inspectionRepository.findById(inspection.getId())).willReturn(Optional.of(inspection));

            assertThatThrownBy(() -> inspectionService.getInspection(inspection.getId().value(), UUID.randomUUID(), false))
                    .isInstanceOf(InspectionException.class);
        }

        @Test
        @DisplayName("inspectionId에 해당하는 검수 요청이 없으면 InspectionException을 던진다")
        void getInspection_notFound_throwsException() {
            UUID unknownId = UUID.randomUUID();
            given(inspectionRepository.findById(InspectionId.of(unknownId))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionService.getInspection(unknownId, UUID.randomUUID(), true))
                    .isInstanceOf(InspectionException.class)
                    .hasMessageContaining(unknownId.toString());
        }
    }
}
