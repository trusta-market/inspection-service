package com.trustamarket.inspectionservice.center.adapter.out.messaging;

import com.trustamarket.inspectionservice.center.application.event.SlotAssignedEvent;
import com.trustamarket.inspectionservice.center.application.port.out.SlotEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotEventPublisherAdapter implements SlotEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(SlotAssignedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
