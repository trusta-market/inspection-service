package com.trustamarket.inspectionservice.inspection.application.dto.command;

import java.util.UUID;

public record AcceptPriceCommand(
        UUID productId
) {
}
