package com.trustamarket.inspectionservice.center.application.service;

import com.trustamarket.inspectionservice.center.application.dto.command.RegisterCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.result.ChangeCenterStatusResult;
import com.trustamarket.inspectionservice.center.application.dto.result.RegisterCenterResult;
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
class InspectionCenterServiceTest {

    @Mock
    private InspectionCenterRepository inspectionCenterRepository;

    @InjectMocks
    private InspectionCenterService inspectionCenterService;

    private static final UUID CENTER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InspectionCenter centerInStatus(CenterStatus status) {
        return InspectionCenter.restore(
                CenterId.of(CENTER_ID),
                "서울 검수 센터",
                new Address("강남대로 123", null, "서울", "06000"),
                "02-1234-5678",
                10,
                0,
                status
        );
    }

    @Nested
    @DisplayName("센터 등록 (register)")
    class Register {

        @Test
        @DisplayName("유효한 커맨드로 센터를 등록하면 OPEN 상태의 결과를 반환한다")
        void register_success() {
            RegisterCenterCommand command = new RegisterCenterCommand(
                    "서울 검수 센터",
                    "강남대로 123",
                    "2층",
                    "서울",
                    "06000",
                    "02-1234-5678",
                    10
            );
            given(inspectionCenterRepository.save(any(InspectionCenter.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            RegisterCenterResult result = inspectionCenterService.register(command);

            assertThat(result.centerId()).isNotNull();
            assertThat(result.name()).isEqualTo("서울 검수 센터");
            assertThat(result.status()).isEqualTo(CenterStatus.OPEN);
        }

        @Test
        @DisplayName("등록된 센터는 초기 부하 0, OPEN 상태로 저장된다")
        void register_savesWithInitialState() {
            RegisterCenterCommand command = new RegisterCenterCommand(
                    "부산 검수 센터",
                    "해운대로 456",
                    null,
                    "부산",
                    "48000",
                    "051-9876-5432",
                    5
            );
            given(inspectionCenterRepository.save(any(InspectionCenter.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            inspectionCenterService.register(command);

            ArgumentCaptor<InspectionCenter> captor = ArgumentCaptor.forClass(InspectionCenter.class);
            then(inspectionCenterRepository).should().save(captor.capture());

            InspectionCenter saved = captor.getValue();
            assertThat(saved.getName()).isEqualTo("부산 검수 센터");
            assertThat(saved.getCapacity()).isEqualTo(5);
            assertThat(saved.getCurrentLoad()).isEqualTo(0);
            assertThat(saved.getStatus()).isEqualTo(CenterStatus.OPEN);
        }

        @Test
        @DisplayName("센터명이 빈 문자열이면 InspectionCenterException을 던진다")
        void register_blankName_throwsException() {
            RegisterCenterCommand command = new RegisterCenterCommand(
                    "",
                    "강남대로 123",
                    null,
                    "서울",
                    "06000",
                    "02-1234-5678",
                    10
            );

            assertThatThrownBy(() -> inspectionCenterService.register(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("capacity가 0이면 InspectionCenterException을 던진다")
        void register_zeroCapacity_throwsException() {
            RegisterCenterCommand command = new RegisterCenterCommand(
                    "서울 검수 센터",
                    "강남대로 123",
                    null,
                    "서울",
                    "06000",
                    "02-1234-5678",
                    0
            );

            assertThatThrownBy(() -> inspectionCenterService.register(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("capacity");
        }

        @Test
        @DisplayName("주소 line1이 빈 문자열이면 InspectionCenterException을 던진다")
        void register_blankAddressLine1_throwsException() {
            RegisterCenterCommand command = new RegisterCenterCommand(
                    "서울 검수 센터",
                    "",
                    null,
                    "서울",
                    "06000",
                    "02-1234-5678",
                    10
            );

            assertThatThrownBy(() -> inspectionCenterService.register(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("line1");
        }
    }

    @Nested
    @DisplayName("센터 오픈 (open)")
    class Open {

        @Test
        @DisplayName("MAINTENANCE 상태의 센터를 OPEN으로 전환한다")
        void open_success() {
            InspectionCenter center = centerInStatus(CenterStatus.MAINTENANCE);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            given(inspectionCenterRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            ChangeCenterStatusResult result = inspectionCenterService.open(CENTER_ID);

            assertThat(result.centerId()).isEqualTo(CENTER_ID);
            assertThat(result.status()).isEqualTo(CenterStatus.OPEN);
        }

        @Test
        @DisplayName("존재하지 않는 센터 ID면 InspectionCenterException을 던진다")
        void open_notFound_throwsException() {
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionCenterService.open(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class);
        }

        @Test
        @DisplayName("OPEN 상태의 센터에 open()을 호출하면 InspectionCenterException을 던진다")
        void open_invalidStatus_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));

            assertThatThrownBy(() -> inspectionCenterService.open(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class);
        }
    }

    @Nested
    @DisplayName("유지보수 전환 (startMaintenance)")
    class StartMaintenance {

        @Test
        @DisplayName("OPEN 상태의 센터를 MAINTENANCE로 전환한다")
        void startMaintenance_success() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            given(inspectionCenterRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            ChangeCenterStatusResult result = inspectionCenterService.startMaintenance(CENTER_ID);

            assertThat(result.centerId()).isEqualTo(CENTER_ID);
            assertThat(result.status()).isEqualTo(CenterStatus.MAINTENANCE);
        }

        @Test
        @DisplayName("존재하지 않는 센터 ID면 InspectionCenterException을 던진다")
        void startMaintenance_notFound_throwsException() {
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionCenterService.startMaintenance(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class);
        }

        @Test
        @DisplayName("MAINTENANCE 상태의 센터에 startMaintenance()를 호출하면 InspectionCenterException을 던진다")
        void startMaintenance_invalidStatus_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.MAINTENANCE);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));

            assertThatThrownBy(() -> inspectionCenterService.startMaintenance(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class);
        }
    }

    @Nested
    @DisplayName("센터 폐쇄 (close)")
    class Close {

        @Test
        @DisplayName("MAINTENANCE 상태의 센터를 CLOSED로 전환한다")
        void close_success() {
            InspectionCenter center = centerInStatus(CenterStatus.MAINTENANCE);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            given(inspectionCenterRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            ChangeCenterStatusResult result = inspectionCenterService.close(CENTER_ID);

            assertThat(result.centerId()).isEqualTo(CENTER_ID);
            assertThat(result.status()).isEqualTo(CenterStatus.CLOSED);
        }

        @Test
        @DisplayName("존재하지 않는 센터 ID면 InspectionCenterException을 던진다")
        void close_notFound_throwsException() {
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionCenterService.close(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class);
        }

        @Test
        @DisplayName("OPEN 상태의 센터에 close()를 호출하면 InspectionCenterException을 던진다")
        void close_invalidStatus_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));

            assertThatThrownBy(() -> inspectionCenterService.close(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class);
        }
    }
}
