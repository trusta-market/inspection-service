package com.trustamarket.inspectionservice.inspection.application.port.out;

import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;

import java.util.Optional;

public interface InspectionRepository {

    Inspection save(Inspection inspection);

    Optional<Inspection> findById(InspectionId id);
}
