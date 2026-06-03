package com.trustamarket.inspectionservice.inspection.application.service;

import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.application.port.out.InboxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class InboxMessageHandlerTest {

    @Mock
    private InboxRepository inboxRepository;

    @InjectMocks
    private InboxMessageHandler handler;

    private static final UUID EVENT_ID = UUID.fromString("e0000000-0000-0000-0000-000000000009");
    private static final String GROUP = "inspection-service";

    @Test
    @DisplayName("처음 보는 이벤트면 domainWork를 실행하고 inbox에 기록한 뒤 true를 반환한다")
    void process_firstTime_runsWorkAndRecords() {
        given(inboxRepository.isAlreadyProcessed(EVENT_ID, GROUP)).willReturn(false);
        AtomicBoolean ran = new AtomicBoolean(false);

        boolean processed = handler.process(EVENT_ID, GROUP, InboxPurpose.INSPECTION_REQUESTED, () -> ran.set(true));

        assertThat(processed).isTrue();
        assertThat(ran).isTrue();
        then(inboxRepository).should().record(EVENT_ID, GROUP, InboxPurpose.INSPECTION_REQUESTED);
    }

    @Test
    @DisplayName("이미 처리한 이벤트(중복)면 domainWork를 실행하지 않고 기록도 없이 false를 반환한다")
    void process_duplicate_skipsWork() {
        given(inboxRepository.isAlreadyProcessed(EVENT_ID, GROUP)).willReturn(true);
        AtomicBoolean ran = new AtomicBoolean(false);

        boolean processed = handler.process(EVENT_ID, GROUP, InboxPurpose.INSPECTION_REQUESTED, () -> ran.set(true));

        assertThat(processed).isFalse();
        assertThat(ran).isFalse();
        then(inboxRepository).should(never()).record(any(), any(), any());
    }
}
