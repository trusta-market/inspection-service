package com.trustamarket.inspectionservice.center.application.dto.command;

public record RegisterCenterCommand(
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String contactPhone
) {}
