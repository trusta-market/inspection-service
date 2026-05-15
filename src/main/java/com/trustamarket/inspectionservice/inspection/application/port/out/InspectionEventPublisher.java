package com.trustamarket.inspectionservice.inspection.application.port.out;

import com.trustamarket.inspectionservice.inspection.application.event.InspectionCompletedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionFailedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionReturnCompletedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionStartedEvent;

public interface InspectionEventPublisher {

    void publish(InspectionStartedEvent event);

    void publish(InspectionCompletedEvent event);

    void publish(InspectionFailedEvent event);

    void publish(InspectionReturnCompletedEvent event);
}
