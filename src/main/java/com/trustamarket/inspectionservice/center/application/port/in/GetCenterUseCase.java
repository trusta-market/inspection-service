package com.trustamarket.inspectionservice.center.application.port.in;

import com.trustamarket.inspectionservice.center.application.dto.query.GetCentersQuery;
import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterPageResult;
import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterResult;

import java.util.UUID;

public interface GetCenterUseCase {

    GetCenterResult getCenter(UUID centerId);

    GetCenterPageResult getCenters(GetCentersQuery query);
}
