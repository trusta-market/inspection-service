package com.trustamarket.inspectionservice.center.application.port.out;

import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;

import java.util.Optional;

public interface InspectionCenterRepository {

    InspectionCenter save(InspectionCenter center);

    Optional<InspectionCenter> findById(CenterId id);

    boolean existsByNameAndAddress(String name, String addressLine1, String postalCode);

    void delete(CenterId id, String deletedBy);
}
