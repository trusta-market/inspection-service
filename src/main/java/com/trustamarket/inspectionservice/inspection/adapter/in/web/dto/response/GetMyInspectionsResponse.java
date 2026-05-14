package com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionPageResult;

import java.util.List;

public record GetMyInspectionsResponse(
        List<GetInspectionSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static GetMyInspectionsResponse from(GetInspectionPageResult result) {
        return new GetMyInspectionsResponse(
                result.content().stream().map(GetInspectionSummaryResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
