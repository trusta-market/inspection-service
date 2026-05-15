package com.trustamarket.inspectionservice.inspection.adapter.in.web;

import com.trustamarket.common.response.CommonResponse;
import com.trustamarket.inspectionservice.inspection.adapter.in.web.dto.response.GetCenterIdByProductIdResponse;
import com.trustamarket.inspectionservice.inspection.application.port.in.GetCenterIdByProductIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/inspections")
@RequiredArgsConstructor
public class InspectionInternalApiController {

    private final GetCenterIdByProductIdUseCase getCenterIdByProductIdUseCase;

    @GetMapping("/center-id")
    public ResponseEntity<CommonResponse<GetCenterIdByProductIdResponse>> getCenterIdByProductId(
            @RequestParam UUID productId) {
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK.value(),
                GetCenterIdByProductIdResponse.from(getCenterIdByProductIdUseCase.getCenterId(productId))));
    }
}
