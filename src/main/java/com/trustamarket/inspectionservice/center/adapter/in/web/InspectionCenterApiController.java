package com.trustamarket.inspectionservice.center.adapter.in.web;

import com.trustamarket.inspectionservice.center.adapter.in.web.dto.request.RegisterCenterRequest;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.ChangeCenterStatusResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.GetCenterResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.GetCentersResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.RegisterCenterResponse;
import com.trustamarket.inspectionservice.center.application.dto.query.GetCentersQuery;
import com.trustamarket.inspectionservice.center.application.port.in.InspectionCenterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/centers")
@RequiredArgsConstructor
public class InspectionCenterApiController {

    private final InspectionCenterUseCase inspectionCenterUseCase;

    @GetMapping("/{centerId}")
    public ResponseEntity<GetCenterResponse> getCenter(@PathVariable UUID centerId) {
        return ResponseEntity.ok(GetCenterResponse.from(inspectionCenterUseCase.getCenter(centerId)));
    }

    @GetMapping
    public ResponseEntity<GetCentersResponse> getCenters(Pageable pageable) {
        GetCentersQuery query = new GetCentersQuery(pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(GetCentersResponse.from(inspectionCenterUseCase.getCenters(query)));
    }

    @PostMapping
    public ResponseEntity<RegisterCenterResponse> register(@Valid @RequestBody RegisterCenterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RegisterCenterResponse.from(inspectionCenterUseCase.register(request.toCommand())));
    }

    @PostMapping("/{centerId}/open")
    public ResponseEntity<ChangeCenterStatusResponse> open(@PathVariable UUID centerId) {
        return ResponseEntity.ok(ChangeCenterStatusResponse.from(inspectionCenterUseCase.open(centerId)));
    }

    @PostMapping("/{centerId}/maintenance")
    public ResponseEntity<ChangeCenterStatusResponse> startMaintenance(@PathVariable UUID centerId) {
        return ResponseEntity.ok(ChangeCenterStatusResponse.from(inspectionCenterUseCase.startMaintenance(centerId)));
    }

    @PostMapping("/{centerId}/close")
    public ResponseEntity<ChangeCenterStatusResponse> close(@PathVariable UUID centerId) {
        return ResponseEntity.ok(ChangeCenterStatusResponse.from(inspectionCenterUseCase.close(centerId)));
    }

    @DeleteMapping("/{centerId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID centerId,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "system") String userId
    ) {
        inspectionCenterUseCase.delete(centerId, userId);
        return ResponseEntity.noContent().build();
    }
}
