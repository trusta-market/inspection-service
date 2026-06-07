package com.trustamarket.inspectionservice.inspection.application.port.out;

import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionSummaryResult;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import com.trustamarket.inspectionservice.inspection.domain.vo.ProductId;
import com.trustamarket.inspectionservice.inspection.domain.vo.SellerId;

import java.util.List;
import java.util.Optional;

public interface InspectionRepository {

    Inspection save(Inspection inspection);

    Optional<Inspection> findById(InspectionId id);

    Optional<Inspection> findByProductId(ProductId productId);

    List<GetInspectionSummaryResult> findSummariesBySellerId(SellerId sellerId, int page, int size);

    long countBySellerId(SellerId sellerId);
}
