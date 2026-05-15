package com.trustamarket.inspectionservice.inspection.adapter.in.web;

import com.trustamarket.common.response.CommonResponse;
import com.trustamarket.common.util.SecurityUtil;
import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.request.CompleteInspectionRequest;
import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.request.FailInspectionRequest;
import com.trustamarket.inspectionservice.inspection.application.dto.command.StartInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.CompleteInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.FailInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.StartInspectionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/inspections")
@RequiredArgsConstructor
public class InspectionAdminApiController {

    private final StartInspectionUseCase startInspectionUseCase;
    private final CompleteInspectionUseCase completeInspectionUseCase;
    private final FailInspectionUseCase failInspectionUseCase;

    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    @PostMapping("/{inspectionId}/start")
    public ResponseEntity<CommonResponse<Void>> start(@PathVariable UUID inspectionId) {
        UUID inspectorId = SecurityUtil.getCurrentUserIdOrThrow();
        startInspectionUseCase.start(StartInspectionCommand.of(inspectionId, inspectorId));
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), null));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    @PostMapping("/{inspectionId}/complete")
    public ResponseEntity<CommonResponse<Void>> complete(
            @PathVariable UUID inspectionId,
            @RequestBody CompleteInspectionRequest request
    ) {
        completeInspectionUseCase.complete(request.toCommand(inspectionId));
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), null));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    @PostMapping("/{inspectionId}/fail")
    public ResponseEntity<CommonResponse<Void>> fail(
            @PathVariable UUID inspectionId,
            @RequestBody FailInspectionRequest request
    ) {
        failInspectionUseCase.fail(request.toCommand(inspectionId));
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), null));
    }
}
