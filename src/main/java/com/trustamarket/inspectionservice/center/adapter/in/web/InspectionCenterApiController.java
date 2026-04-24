package com.trustamarket.inspectionservice.center.adapter.in.web;

import com.trustamarket.inspectionservice.center.adapter.in.web.dto.request.RegisterCenterRequest;
import com.trustamarket.inspectionservice.center.adapter.in.web.dto.response.RegisterCenterResponse;
import com.trustamarket.inspectionservice.center.application.port.in.RegisterCenterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/centers")
@RequiredArgsConstructor
public class InspectionCenterApiController {

    private final RegisterCenterUseCase registerCenterUseCase;

    @PostMapping
    public ResponseEntity<RegisterCenterResponse> register(@Valid @RequestBody RegisterCenterRequest request) {
        RegisterCenterResponse response = RegisterCenterResponse.from(registerCenterUseCase.register(request.toCommand()));
        return ResponseEntity.ok(response);
    }
}
