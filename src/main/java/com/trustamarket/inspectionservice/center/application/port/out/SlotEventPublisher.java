package com.trustamarket.inspectionservice.center.application.port.out;

import com.trustamarket.inspectionservice.center.application.event.SlotAssignedEvent;

public interface SlotEventPublisher {
    void publish(SlotAssignedEvent event);
}
