package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InspectionJpaRepository extends JpaRepository<InspectionJpaEntity, UUID> {

    Optional<InspectionJpaEntity> findByInspectionIdAndDeletedAtIsNull(UUID inspectionId);

    Optional<InspectionJpaEntity> findByProductIdAndDeletedAtIsNull(UUID productId);

    @Query("""
            SELECT new com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa.InspectionSummaryProjection(
                i.inspectionId, i.productId, i.centerId,
                i.originalPriceAmount, i.originalPriceCurrency, i.status, i.requestedAt)
            FROM InspectionJpaEntity i
            WHERE i.sellerId = :sellerId AND i.deletedAt IS NULL
            """)
    List<InspectionSummaryProjection> findSummariesBySellerId(@Param("sellerId") UUID sellerId, Pageable pageable);

    long countBySellerIdAndDeletedAtIsNull(UUID sellerId);
}
