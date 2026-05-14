package com.trustamarket.inspectionservice.inspection.adapter.in.web;

import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.request.CompleteInspectionRequest;
import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.request.StartInspectionRequest;
import com.trustamarket.inspectionservice.inspection.application.port.in.CompleteInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.StartInspectionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{inspectionId}/start")
    public ResponseEntity<Void> start(
            @PathVariable UUID inspectionId,
            @RequestBody StartInspectionRequest request
    ) {
        startInspectionUseCase.start(request.toCommand(inspectionId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{inspectionId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID inspectionId,
            @RequestBody CompleteInspectionRequest request
    ) {
        completeInspectionUseCase.complete(request.toCommand(inspectionId));
        return ResponseEntity.ok().build();
    }
}
