package com.trustamarket.inspectionservice.center.adapter.in.web.dto.request;

import com.trustamarket.inspectionservice.center.application.dto.command.RegisterCenterCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCenterRequest(
        @NotBlank String name,
        @NotBlank String addressLine1,
        String addressLine2,
        @NotBlank String city,
        @NotBlank String postalCode,
        String contactPhone,
        @NotNull @Min(1) Integer capacity
) {
    public RegisterCenterCommand toCommand() {
        return new RegisterCenterCommand(name, addressLine1, addressLine2, city, postalCode, contactPhone, capacity);
    }
}
