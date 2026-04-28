package com.trustamarket.inspectionservice.center.adapter.in.web.dto.response;

import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterPageResult;

import java.util.List;

public record GetCentersResponse(
        List<GetCenterResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static GetCentersResponse from(GetCenterPageResult result) {
        return new GetCentersResponse(
                result.content().stream().map(GetCenterResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
