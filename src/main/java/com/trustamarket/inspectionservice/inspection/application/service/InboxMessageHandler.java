package com.trustamarket.inspectionservice.inspection.application.service;

import com.trustamarket.inspectionservice.inspection.application.port.out.InboxPurpose;
import com.trustamarket.inspectionservice.inspection.application.port.out.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InboxMessageHandler {

    private final InboxRepository inboxRepository;

    @Transactional
    public boolean process(UUID eventId, String consumerGroup, InboxPurpose purpose, Runnable domainWork) {
        if (inboxRepository.isAlreadyProcessed(eventId, consumerGroup)) {
            return false;
        }
        domainWork.run();
        inboxRepository.record(eventId, consumerGroup, purpose);
        return true;
    }
}
