package com.trustamarket.inspectionservice.center.application.dto.command;

import java.util.UUID;

public record UpdateCenterCommand(
        UUID centerId,
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String contactPhone
) {}
