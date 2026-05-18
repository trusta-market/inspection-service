package com.trustamarket.inspectionservice.inspection.adapter.in.web;

import com.trustamarket.common.response.CommonResponse;
import com.trustamarket.common.util.SecurityUtil;
import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response.GetInspectionResponse;
import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response.GetMyInspectionsResponse;
import com.trustamarket.inspectionservice.inspection.application.dto.query.GetMyInspectionsQuery;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionResult;
import com.trustamarket.inspectionservice.inspection.application.port.in.GetInspectionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionApiController {

    private final GetInspectionUseCase getInspectionUseCase;

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<GetMyInspectionsResponse>> getMyInspections(Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserIdOrThrow();
        GetMyInspectionsQuery query = new GetMyInspectionsQuery(userId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), GetMyInspectionsResponse.from(getInspectionUseCase.getMyInspections(query))));
    }

    @PreAuthorize("hasAnyRole('MEMBER', 'INSPECTOR', 'ADMIN')")
    @GetMapping("/{inspectionId}")
    public ResponseEntity<CommonResponse<GetInspectionResponse>> getInspection(@PathVariable UUID inspectionId) {
        UUID userId = SecurityUtil.getCurrentUserIdOrThrow();
        boolean isPrivileged = SecurityUtil.getCurrentUser()
                .map(u -> u.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_INSPECTOR") || a.getAuthority().equals("ROLE_ADMIN")))
                .orElse(false);
        GetInspectionResult result = getInspectionUseCase.getInspection(inspectionId, userId, isPrivileged);
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), GetInspectionResponse.from(result)));
    }
}
