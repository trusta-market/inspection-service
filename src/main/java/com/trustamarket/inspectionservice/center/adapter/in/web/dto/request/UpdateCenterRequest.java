package com.trustamarket.inspectionservice.center.adapter.in.web.dto.request;

import com.trustamarket.inspectionservice.center.application.dto.command.UpdateCenterCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCenterRequest(
        @NotBlank String name,
        @NotBlank String addressLine1,
        String addressLine2,
        @NotBlank String city,
        @NotBlank String postalCode,
        String contactPhone
) {
    public UpdateCenterCommand toCommand(UUID centerId) {
        return new UpdateCenterCommand(centerId, name, addressLine1, addressLine2, city, postalCode, contactPhone);
    }
}
