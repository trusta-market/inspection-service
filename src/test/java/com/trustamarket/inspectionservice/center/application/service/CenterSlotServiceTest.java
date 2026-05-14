package com.trustamarket.inspectionservice.center.application.service;

import com.trustamarket.inspectionservice.center.application.dto.command.AssignCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.result.ReserveSlotResult;
import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.domain.enums.CenterStatus;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.Address;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.trustamarket.inspectionservice.center.application.port.out.SlotEventPublisher;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CenterSlotServiceTest {

    @Mock
    private InspectionCenterRepository centerRepository;

    @Mock
    private SlotEventPublisher slotEventPublisher;

    @InjectMocks
    private CenterSlotService centerSlotService;

    private static final UUID CENTER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InspectionCenter centerWithLoad(int capacity, int currentLoad) {
        return InspectionCenter.restore(
                CenterId.of(CENTER_ID),
                "서울 검수 센터",
                new Address("강남대로 123", null, "서울", "06000"),
                "02-1234-5678",
                capacity,
                currentLoad,
                CenterStatus.OPEN
        );
    }

    @Nested
    @DisplayName("슬롯 예약 (reserveSlot)")
    class ReserveSlot {

        @Test
        @DisplayName("가용 센터가 있으면 슬롯을 예약하고 centerId를 반환한다")
        void reserveSlot_success() {
            InspectionCenter center = centerWithLoad(10, 3);
            given(centerRepository.findAvailableWithLock()).willReturn(Optional.of(center));
            given(centerRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            ReserveSlotResult result = centerSlotService.reserveSlot();

            assertThat(result.centerId()).isEqualTo(CENTER_ID);
        }

        @Test
        @DisplayName("슬롯 예약 후 currentLoad가 1 증가한 채로 저장된다")
        void reserveSlot_incrementsCurrentLoad() {
            InspectionCenter center = centerWithLoad(10, 3);
            given(centerRepository.findAvailableWithLock()).willReturn(Optional.of(center));
            given(centerRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            centerSlotService.reserveSlot();

            ArgumentCaptor<InspectionCenter> captor = ArgumentCaptor.forClass(InspectionCenter.class);
            then(centerRepository).should().save(captor.capture());
            assertThat(captor.getValue().getCurrentLoad()).isEqualTo(4);
        }

        @Test
        @DisplayName("가용 센터가 없으면 InspectionCenterException을 던진다")
        void reserveSlot_noAvailableCenter_throwsException() {
            given(centerRepository.findAvailableWithLock()).willReturn(Optional.empty());

            assertThatThrownBy(() -> centerSlotService.reserveSlot())
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("예약 가능한 검수 센터가 없습니다");
        }

        @Test
        @DisplayName("센터가 만석이면 InspectionCenterException을 던진다")
        void reserveSlot_centerFull_throwsException() {
            InspectionCenter center = centerWithLoad(5, 5);
            given(centerRepository.findAvailableWithLock()).willReturn(Optional.of(center));

            assertThatThrownBy(() -> centerSlotService.reserveSlot())
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("수용량 초과");
        }
    }

    @Nested
    @DisplayName("센터 배정 (assign)")
    class Assign {

        private AssignCenterCommand validCommand() {
            return new AssignCenterCommand(
                    UUID.randomUUID(), UUID.randomUUID(), 1_500_000L, "KRW"
            );
        }

        @Test
        @DisplayName("가용 센터가 있으면 슬롯을 예약하고 이벤트를 발행한다")
        void assign_success_reservesSlotAndPublishesEvent() {
            InspectionCenter center = centerWithLoad(10, 3);
            given(centerRepository.findAvailableWithLock()).willReturn(Optional.of(center));
            given(centerRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            centerSlotService.assign(validCommand());

            ArgumentCaptor<InspectionCenter> captor = ArgumentCaptor.forClass(InspectionCenter.class);
            then(centerRepository).should().save(captor.capture());
            assertThat(captor.getValue().getCurrentLoad()).isEqualTo(4);
            then(slotEventPublisher).should().publish(any());
        }

        @Test
        @DisplayName("가용 센터가 없으면 InspectionCenterException을 던지고 이벤트를 발행하지 않는다")
        void assign_noAvailableCenter_throwsException() {
            given(centerRepository.findAvailableWithLock()).willReturn(Optional.empty());

            assertThatThrownBy(() -> centerSlotService.assign(validCommand()))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("예약 가능한 검수 센터가 없습니다");

            then(slotEventPublisher).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("센터가 만석이면 InspectionCenterException을 던지고 이벤트를 발행하지 않는다")
        void assign_centerFull_throwsException() {
            InspectionCenter center = centerWithLoad(5, 5);
            given(centerRepository.findAvailableWithLock()).willReturn(Optional.of(center));

            assertThatThrownBy(() -> centerSlotService.assign(validCommand()))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("수용량 초과");

            then(slotEventPublisher).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("슬롯 해제 (releaseSlot)")
    class ReleaseSlot {

        @Test
        @DisplayName("센터가 존재하면 슬롯을 해제하고 save를 호출한다")
        void releaseSlot_success() {
            InspectionCenter center = centerWithLoad(10, 5);
            given(centerRepository.findByIdWithLock(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            given(centerRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            centerSlotService.releaseSlot(CENTER_ID);

            then(centerRepository).should().save(any(InspectionCenter.class));
        }

        @Test
        @DisplayName("슬롯 해제 후 currentLoad가 1 감소한 채로 저장된다")
        void releaseSlot_decrementsCurrentLoad() {
            InspectionCenter center = centerWithLoad(10, 5);
            given(centerRepository.findByIdWithLock(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            given(centerRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            centerSlotService.releaseSlot(CENTER_ID);

            ArgumentCaptor<InspectionCenter> captor = ArgumentCaptor.forClass(InspectionCenter.class);
            then(centerRepository).should().save(captor.capture());
            assertThat(captor.getValue().getCurrentLoad()).isEqualTo(4);
        }

        @Test
        @DisplayName("존재하지 않는 centerId면 InspectionCenterException을 던진다")
        void releaseSlot_notFound_throwsException() {
            given(centerRepository.findByIdWithLock(CenterId.of(CENTER_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> centerSlotService.releaseSlot(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("존재하지 않는 센터입니다");
        }

        @Test
        @DisplayName("currentLoad가 0인 센터에 해제를 시도하면 InspectionCenterException을 던진다")
        void releaseSlot_zeroLoad_throwsException() {
            InspectionCenter center = centerWithLoad(10, 0);
            given(centerRepository.findByIdWithLock(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));

            assertThatThrownBy(() -> centerSlotService.releaseSlot(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("0인 센터는 release");
        }
    }
}
