package com.trustamarket.inspectionservice.inspection.adapter.in.web;

import com.trustamarket.common.config.security.UserDetailsImpl;
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
import org.springframework.security.access.AccessDeniedException;
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
        GetInspectionResult result = getInspectionUseCase.getInspection(inspectionId);
        UserDetailsImpl currentUser = SecurityUtil.getCurrentUser().orElseThrow(() -> new AccessDeniedException("인증이 필요합니다."));
        boolean isMember = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
        if (isMember && !result.sellerId().equals(currentUser.getUuid())) {
            throw new AccessDeniedException("본인의 검수 정보만 조회할 수 있습니다.");
        }
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), GetInspectionResponse.from(result)));
    }
}
