package com.trustamarket.inspectionservice.inspection.application.dto.query;

import java.util.UUID;

public record GetMyInspectionsQuery(
        UUID userId,
        int page,
        int size
) {
}
