package com.trustamarket.inspectionservice.center.application.port.in;

import java.util.UUID;

public interface DeleteCenterUseCase {

    void delete(UUID centerId, String deletedBy);
}
