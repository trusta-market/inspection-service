package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.Address;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import org.springframework.stereotype.Component;

@Component
public class InspectionCenterMapper {

    public InspectionCenterJpaEntity toJpaEntity(InspectionCenter center) {
        return InspectionCenterJpaEntity.of(
                center.getId().value(),
                center.getName(),
                center.getAddress().line1(),
                center.getAddress().line2(),
                center.getAddress().city(),
                center.getAddress().postalCode(),
                center.getContactPhone(),
                center.getCapacity(),
                center.getCurrentLoad(),
                center.getStatus()
        );
    }

    public InspectionCenter toDomain(InspectionCenterJpaEntity entity) {
        return InspectionCenter.restore(
                CenterId.of(entity.getCenterId()),
                entity.getName(),
                new Address(entity.getAddressLine1(), entity.getAddressLine2(), entity.getCity(), entity.getPostalCode()),
                entity.getContactPhone(),
                entity.getCapacity(),
                entity.getCurrentLoad(),
                entity.getStatus()
        );
    }
}
