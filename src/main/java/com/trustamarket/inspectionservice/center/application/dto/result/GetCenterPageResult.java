package com.trustamarket.inspectionservice.center.application.dto.result;

import java.util.List;

public record GetCenterPageResult(
        List<GetCenterResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
