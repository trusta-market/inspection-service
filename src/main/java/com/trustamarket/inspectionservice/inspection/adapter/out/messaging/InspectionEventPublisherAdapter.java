package com.trustamarket.inspectionservice.inspection.adapter.out.messaging;

import com.trustamarket.inspectionservice.inspection.application.event.InspectionStartedEvent;
import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InspectionEventPublisherAdapter implements InspectionEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(InspectionStartedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
