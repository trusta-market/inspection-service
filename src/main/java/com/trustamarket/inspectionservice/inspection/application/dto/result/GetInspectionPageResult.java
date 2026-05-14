package com.trustamarket.inspectionservice.inspection.application.dto.result;

import java.util.List;

public record GetInspectionPageResult(
        List<GetInspectionSummaryResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
