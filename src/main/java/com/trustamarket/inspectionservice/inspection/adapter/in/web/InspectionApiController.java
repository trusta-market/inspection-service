package com.trustamarket.inspectionservice.inspection.adapter.in.web;

import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response.GetInspectionResponse;
import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response.GetMyInspectionsResponse;
import com.trustamarket.inspectionservice.inspection.application.dto.query.GetMyInspectionsQuery;
import com.trustamarket.inspectionservice.inspection.application.port.in.GetInspectionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionApiController {

    private final GetInspectionUseCase getInspectionUseCase;

    @GetMapping("/me")
    public ResponseEntity<GetMyInspectionsResponse> getMyInspections(
            @RequestHeader("X-User-Id") UUID userId,
            Pageable pageable
    ) {
        GetMyInspectionsQuery query = new GetMyInspectionsQuery(userId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(GetMyInspectionsResponse.from(getInspectionUseCase.getMyInspections(query)));
    }

    @GetMapping("/{inspectionId}")
    public ResponseEntity<GetInspectionResponse> getInspection(@PathVariable UUID inspectionId) {
        return ResponseEntity.ok(GetInspectionResponse.from(getInspectionUseCase.getInspection(inspectionId)));
    }
}
