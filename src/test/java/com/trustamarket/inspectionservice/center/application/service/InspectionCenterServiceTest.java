package com.trustamarket.inspectionservice.center.application.service;

import com.trustamarket.inspectionservice.center.application.dto.command.RegisterCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.result.RegisterCenterResult;
import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.domain.model.enums.CenterStatus;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class InspectionCenterServiceTest {

    @Mock
    private InspectionCenterRepository inspectionCenterRepository;

    @InjectMocks
    private InspectionCenterService inspectionCenterService;

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
}
