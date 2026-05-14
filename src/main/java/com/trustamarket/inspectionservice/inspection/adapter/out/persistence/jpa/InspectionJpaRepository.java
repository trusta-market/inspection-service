package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InspectionJpaRepository extends JpaRepository<InspectionJpaEntity, UUID> {

    Optional<InspectionJpaEntity> findByInspectionIdAndDeletedAtIsNull(UUID inspectionId);

    Optional<InspectionJpaEntity> findByProductIdAndDeletedAtIsNull(UUID productId);

    List<InspectionJpaEntity> findBySellerIdAndDeletedAtIsNull(UUID sellerId, Pageable pageable);

    long countBySellerIdAndDeletedAtIsNull(UUID sellerId);
}
