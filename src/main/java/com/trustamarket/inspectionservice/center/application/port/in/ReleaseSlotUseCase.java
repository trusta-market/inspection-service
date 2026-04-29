package com.trustamarket.inspectionservice.center.application.port.in;

import java.util.UUID;

public interface ReleaseSlotUseCase {
    void releaseSlot(UUID centerId);
}
