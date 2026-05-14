package com.trustamarket.inspectionservice.inspection.application.port.out;

import com.trustamarket.inspectionservice.inspection.application.event.InspectionStartedEvent;

public interface InspectionEventPublisher {
    void publish(InspectionStartedEvent event);
}
