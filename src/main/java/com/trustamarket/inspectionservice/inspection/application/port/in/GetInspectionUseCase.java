package com.trustamarket.inspectionservice.inspection.application.port.in;

import com.trustamarket.inspectionservice.inspection.application.dto.query.GetMyInspectionsQuery;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionPageResult;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionResult;

import java.util.UUID;

public interface GetInspectionUseCase {

    GetInspectionPageResult getMyInspections(GetMyInspectionsQuery query);

    GetInspectionResult getInspection(UUID inspectionId);
}
