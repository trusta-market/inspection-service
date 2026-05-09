package com.trustamarket.inspectionservice.center.application.service;

import com.trustamarket.inspectionservice.center.application.dto.command.RegisterCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.command.UpdateCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.query.GetCentersQuery;
import com.trustamarket.inspectionservice.center.application.dto.result.ChangeCenterStatusResult;
import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterPageResult;
import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterResult;
import com.trustamarket.inspectionservice.center.application.dto.result.RegisterCenterResult;
import com.trustamarket.inspectionservice.center.application.dto.result.UpdateCenterResult;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.willDoNothing;

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

    private InspectionCenter centerWithLoad(int currentLoad) {
        return InspectionCenter.restore(
                CenterId.of(CENTER_ID),
                "서울 검수 센터",
                new Address("강남대로 123", null, "서울", "06000"),
                "02-1234-5678",
                10,
                currentLoad,
                CenterStatus.OPEN
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
        @DisplayName("동일한 이름과 주소의 센터가 이미 존재하면 InspectionCenterException을 던지고 저장하지 않는다")
        void register_duplicateCenter_throwsException() {
            RegisterCenterCommand command = new RegisterCenterCommand(
                    "서울 검수 센터",
                    "강남대로 123",
                    null,
                    "서울",
                    "06000",
                    "02-1234-5678",
                    10
            );
            given(inspectionCenterRepository.existsByNameAndAddress(anyString(), anyString(), anyString()))
                    .willReturn(true);

            assertThatThrownBy(() -> inspectionCenterService.register(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("이미 존재하는 검사 센터입니다");

            then(inspectionCenterRepository).should(never()).save(any());
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

    @Nested
    @DisplayName("센터 삭제 (delete)")
    class Delete {

        private static final String DELETED_BY = "admin-user";

        @Test
        @DisplayName("CLOSED 상태의 센터를 삭제하면 repository.delete()가 호출된다")
        void delete_success() {
            InspectionCenter center = centerInStatus(CenterStatus.CLOSED);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            willDoNothing().given(inspectionCenterRepository).delete(eq(CenterId.of(CENTER_ID)), eq(DELETED_BY));

            inspectionCenterService.delete(CENTER_ID, DELETED_BY);

            then(inspectionCenterRepository).should().delete(CenterId.of(CENTER_ID), DELETED_BY);
        }

        @Test
        @DisplayName("존재하지 않는 센터 ID면 InspectionCenterException을 던진다")
        void delete_notFound_throwsException() {
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionCenterService.delete(CENTER_ID, DELETED_BY))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("존재하지 않는 센터");
        }

        @Test
        @DisplayName("OPEN 상태의 센터를 삭제하면 InspectionCenterException을 던진다")
        void delete_openStatus_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));

            assertThatThrownBy(() -> inspectionCenterService.delete(CENTER_ID, DELETED_BY))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("CLOSED");
        }

        @Test
        @DisplayName("MAINTENANCE 상태의 센터를 삭제하면 InspectionCenterException을 던진다")
        void delete_maintenanceStatus_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.MAINTENANCE);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));

            assertThatThrownBy(() -> inspectionCenterService.delete(CENTER_ID, DELETED_BY))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("CLOSED");
        }
    }

    @Nested
    @DisplayName("센터 단건 조회 (getCenter)")
    class GetCenter {

        @Test
        @DisplayName("존재하는 센터 ID로 조회하면 센터 정보를 반환한다")
        void getCenter_success() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));

            GetCenterResult result = inspectionCenterService.getCenter(CENTER_ID);

            assertThat(result.centerId()).isEqualTo(CENTER_ID);
            assertThat(result.name()).isEqualTo("서울 검수 센터");
            assertThat(result.addressLine1()).isEqualTo("강남대로 123");
            assertThat(result.city()).isEqualTo("서울");
            assertThat(result.capacity()).isEqualTo(10);
            assertThat(result.currentLoad()).isEqualTo(0);
            assertThat(result.status()).isEqualTo(CenterStatus.OPEN);
        }

        @Test
        @DisplayName("존재하지 않는 센터 ID면 InspectionCenterException을 던진다")
        void getCenter_notFound_throwsException() {
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionCenterService.getCenter(CENTER_ID))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("존재하지 않는 센터");
        }
    }

    @Nested
    @DisplayName("센터 페이징 조회 (getCenters)")
    class GetCenters {

        @Test
        @DisplayName("센터 목록을 페이징 조회하면 content와 페이징 메타를 반환한다")
        void getCenters_success() {
            GetCentersQuery query = new GetCentersQuery(0, 10);
            List<InspectionCenter> centers = List.of(
                    centerInStatus(CenterStatus.OPEN),
                    centerInStatus(CenterStatus.MAINTENANCE)
            );
            given(inspectionCenterRepository.findAll(query)).willReturn(centers);
            given(inspectionCenterRepository.countAll()).willReturn(2L);

            GetCenterPageResult result = inspectionCenterService.getCenters(query);

            assertThat(result.content()).hasSize(2);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(2L);
            assertThat(result.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("센터가 없으면 빈 content와 totalElements 0을 반환한다")
        void getCenters_empty() {
            GetCentersQuery query = new GetCentersQuery(0, 10);
            given(inspectionCenterRepository.findAll(query)).willReturn(List.of());
            given(inspectionCenterRepository.countAll()).willReturn(0L);

            GetCenterPageResult result = inspectionCenterService.getCenters(query);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0L);
            assertThat(result.totalPages()).isEqualTo(0);
        }

        @Test
        @DisplayName("totalElements=25, size=10 이면 totalPages는 3이다")
        void getCenters_totalPagesCalculation() {
            GetCentersQuery query = new GetCentersQuery(0, 10);
            given(inspectionCenterRepository.findAll(query)).willReturn(List.of());
            given(inspectionCenterRepository.countAll()).willReturn(25L);

            GetCenterPageResult result = inspectionCenterService.getCenters(query);

            assertThat(result.totalPages()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("센터 정보 변경 (updateCenter)")
    class UpdateCenter {

        private UpdateCenterCommand validCommand() {
            return new UpdateCenterCommand(
                    CENTER_ID,
                    "부산 검수 센터",
                    "해운대로 456",
                    "3층",
                    "부산",
                    "48000",
                    "051-9876-5432",
                    20
            );
        }

        @Test
        @DisplayName("유효한 커맨드로 센터 정보를 변경하면 변경된 결과를 반환한다")
        void updateCenter_success() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            given(inspectionCenterRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            UpdateCenterResult result = inspectionCenterService.updateCenter(validCommand());

            assertThat(result.centerId()).isEqualTo(CENTER_ID);
            assertThat(result.name()).isEqualTo("부산 검수 센터");
            assertThat(result.addressLine1()).isEqualTo("해운대로 456");
            assertThat(result.addressLine2()).isEqualTo("3층");
            assertThat(result.city()).isEqualTo("부산");
            assertThat(result.postalCode()).isEqualTo("48000");
            assertThat(result.contactPhone()).isEqualTo("051-9876-5432");
            assertThat(result.capacity()).isEqualTo(20);
            assertThat(result.status()).isEqualTo(CenterStatus.OPEN);
        }

        @Test
        @DisplayName("변경된 필드가 도메인 모델에 반영되어 저장된다")
        void updateCenter_savesUpdatedFields() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            given(inspectionCenterRepository.save(any(InspectionCenter.class))).willAnswer(inv -> inv.getArgument(0));

            inspectionCenterService.updateCenter(validCommand());

            ArgumentCaptor<InspectionCenter> captor = ArgumentCaptor.forClass(InspectionCenter.class);
            then(inspectionCenterRepository).should().save(captor.capture());

            InspectionCenter saved = captor.getValue();
            assertThat(saved.getName()).isEqualTo("부산 검수 센터");
            assertThat(saved.getAddress().line1()).isEqualTo("해운대로 456");
            assertThat(saved.getAddress().city()).isEqualTo("부산");
            assertThat(saved.getCapacity()).isEqualTo(20);
            assertThat(saved.getContactPhone()).isEqualTo("051-9876-5432");
        }

        @Test
        @DisplayName("존재하지 않는 센터 ID면 InspectionCenterException을 던진다")
        void updateCenter_notFound_throwsException() {
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> inspectionCenterService.updateCenter(validCommand()))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("존재하지 않는 센터");
        }

        @Test
        @DisplayName("이름이 빈 문자열이면 InspectionCenterException을 던진다")
        void updateCenter_blankName_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            UpdateCenterCommand command = new UpdateCenterCommand(CENTER_ID, "", "해운대로 456", null, "부산", "48000", null, 20);

            assertThatThrownBy(() -> inspectionCenterService.updateCenter(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("addressLine1이 빈 문자열이면 InspectionCenterException을 던진다")
        void updateCenter_blankAddressLine1_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            UpdateCenterCommand command = new UpdateCenterCommand(CENTER_ID, "부산 검수 센터", "", null, "부산", "48000", null, 20);

            assertThatThrownBy(() -> inspectionCenterService.updateCenter(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("line1");
        }

        @Test
        @DisplayName("capacity가 0이면 InspectionCenterException을 던진다")
        void updateCenter_zeroCapacity_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            UpdateCenterCommand command = new UpdateCenterCommand(CENTER_ID, "부산 검수 센터", "해운대로 456", null, "부산", "48000", null, 0);

            assertThatThrownBy(() -> inspectionCenterService.updateCenter(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("capacity");
        }

        @Test
        @DisplayName("capacity가 currentLoad보다 작으면 InspectionCenterException을 던진다")
        void updateCenter_capacityBelowCurrentLoad_throwsException() {
            InspectionCenter center = centerWithLoad(5);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            UpdateCenterCommand command = new UpdateCenterCommand(CENTER_ID, "부산 검수 센터", "해운대로 456", null, "부산", "48000", null, 3);

            assertThatThrownBy(() -> inspectionCenterService.updateCenter(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("현재 부하");
        }

        @Test
        @DisplayName("contactPhone이 빈 문자열이면 InspectionCenterException을 던진다")
        void updateCenter_blankContactPhone_throwsException() {
            InspectionCenter center = centerInStatus(CenterStatus.OPEN);
            given(inspectionCenterRepository.findById(CenterId.of(CENTER_ID))).willReturn(Optional.of(center));
            UpdateCenterCommand command = new UpdateCenterCommand(CENTER_ID, "부산 검수 센터", "해운대로 456", null, "부산", "48000", "", 20);

            assertThatThrownBy(() -> inspectionCenterService.updateCenter(command))
                    .isInstanceOf(InspectionCenterException.class)
                    .hasMessageContaining("contactPhone");
        }
    }
}
