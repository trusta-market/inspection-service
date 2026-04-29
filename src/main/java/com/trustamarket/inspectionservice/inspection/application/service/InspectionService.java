package com.trustamarket.inspectionservice.inspection.application.service;

import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.port.in.RequestInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionRepository;
import com.trustamarket.inspectionservice.inspection.domain.enums.CurrencyCode;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import com.trustamarket.inspectionservice.inspection.domain.vo.Money;
import com.trustamarket.inspectionservice.inspection.domain.vo.ProductId;
import com.trustamarket.inspectionservice.inspection.domain.vo.SellerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InspectionService implements RequestInspectionUseCase {

    private final InspectionRepository inspectionRepository;

    @Override
    @Transactional
    public void request(RequestInspectionCommand command) {
        Inspection inspection = Inspection.request(
                InspectionId.generate(),
                ProductId.of(command.productId()),
                SellerId.of(command.sellerId()),
                CenterId.of(command.centerId()),
                Money.of(command.originalPriceAmount(), CurrencyCode.valueOf(command.currency())),
                Instant.now()
        );
        inspectionRepository.save(inspection);
    }
}
