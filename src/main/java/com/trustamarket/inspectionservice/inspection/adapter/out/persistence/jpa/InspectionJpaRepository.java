package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InspectionJpaRepository extends JpaRepository<InspectionJpaEntity, UUID> {

    Optional<InspectionJpaEntity> findByInspectionIdAndDeletedAtIsNull(UUID inspectionId);

    Optional<InspectionJpaEntity> findByProductIdAndDeletedAtIsNull(UUID productId);
}
