package com.trustamarket.inspectionservice.center.adapter.in.web;

import com.trustamarket.common.response.CommonResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.request.RegisterCenterRequest;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.request.UpdateCenterRequest;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.ChangeCenterStatusResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.GetCenterResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.GetCentersResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.RegisterCenterResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.UpdateCenterResponse;
import com.trustamarket.inspectionservice.center.application.dto.query.GetCentersQuery;
import com.trustamarket.inspectionservice.center.application.port.in.InspectionCenterUseCase;
import com.trustamarket.common.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/centers")
@RequiredArgsConstructor
public class InspectionCenterApiController {

    private final InspectionCenterUseCase inspectionCenterUseCase;

    @PreAuthorize("hasAnyRole('MEMBER', 'INSPECTOR', 'ADMIN')")
    @GetMapping("/{centerId}")
    public ResponseEntity<CommonResponse<GetCenterResponse>> getCenter(@PathVariable UUID centerId) {
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), GetCenterResponse.from(inspectionCenterUseCase.getCenter(centerId))));
    }

    @PreAuthorize("hasAnyRole('MEMBER', 'INSPECTOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<CommonResponse<GetCentersResponse>> getCenters(Pageable pageable) {
        GetCentersQuery query = new GetCentersQuery(pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), GetCentersResponse.from(inspectionCenterUseCase.getCenters(query))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{centerId}")
    public ResponseEntity<CommonResponse<UpdateCenterResponse>> updateCenter(
            @PathVariable UUID centerId,
            @Valid @RequestBody UpdateCenterRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), UpdateCenterResponse.from(inspectionCenterUseCase.updateCenter(request.toCommand(centerId)))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CommonResponse<RegisterCenterResponse>> register(@Valid @RequestBody RegisterCenterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(HttpStatus.CREATED.value(), RegisterCenterResponse.from(inspectionCenterUseCase.register(request.toCommand()))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{centerId}/open")
    public ResponseEntity<CommonResponse<ChangeCenterStatusResponse>> open(@PathVariable UUID centerId) {
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), ChangeCenterStatusResponse.from(inspectionCenterUseCase.open(centerId))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{centerId}/maintenance")
    public ResponseEntity<CommonResponse<ChangeCenterStatusResponse>> startMaintenance(@PathVariable UUID centerId) {
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), ChangeCenterStatusResponse.from(inspectionCenterUseCase.startMaintenance(centerId))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{centerId}/close")
    public ResponseEntity<CommonResponse<ChangeCenterStatusResponse>> close(@PathVariable UUID centerId) {
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), ChangeCenterStatusResponse.from(inspectionCenterUseCase.close(centerId))));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{centerId}")
    public ResponseEntity<CommonResponse<Void>> delete(@PathVariable UUID centerId) {
        UUID userId = SecurityUtil.getCurrentUserIdOrThrow();
        inspectionCenterUseCase.delete(centerId, userId.toString());
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(), null));
    }
}
