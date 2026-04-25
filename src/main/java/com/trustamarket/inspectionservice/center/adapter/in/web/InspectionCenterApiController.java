package com.trustamarket.inspectionservice.center.adapter.in.web;

import com.trustamarket.inspectionservice.center.adapter.in.web.dto.request.RegisterCenterRequest;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.ChangeCenterStatusResponse;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.RegisterCenterResponse;
import com.trustamarket.inspectionservice.center.application.port.in.ChangeCenterStatusUseCase;
import com.trustamarket.inspectionservice.center.application.port.in.RegisterCenterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final RegisterCenterUseCase registerCenterUseCase;
    private final ChangeCenterStatusUseCase changeCenterStatusUseCase;

    @PostMapping
    public ResponseEntity<RegisterCenterResponse> register(@Valid @RequestBody RegisterCenterRequest request) {
        RegisterCenterResponse response = RegisterCenterResponse.from(registerCenterUseCase.register(request.toCommand()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{centerId}/open")
    public ResponseEntity<ChangeCenterStatusResponse> open(@PathVariable UUID centerId) {
        return ResponseEntity.ok(ChangeCenterStatusResponse.from(changeCenterStatusUseCase.open(centerId)));
    }

    @PostMapping("/{centerId}/maintenance")
    public ResponseEntity<ChangeCenterStatusResponse> startMaintenance(@PathVariable UUID centerId) {
        return ResponseEntity.ok(ChangeCenterStatusResponse.from(changeCenterStatusUseCase.startMaintenance(centerId)));
    }

    @PostMapping("/{centerId}/close")
    public ResponseEntity<ChangeCenterStatusResponse> close(@PathVariable UUID centerId) {
        return ResponseEntity.ok(ChangeCenterStatusResponse.from(changeCenterStatusUseCase.close(centerId)));
    }
}
