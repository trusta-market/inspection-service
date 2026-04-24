package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InspectionCenterJpaRepository extends JpaRepository<InspectionCenterJpaEntity, UUID> {
    Optional<InspectionCenterJpaEntity> findByCenterIdAndDeletedAtIsNull(UUID centerId);
}
