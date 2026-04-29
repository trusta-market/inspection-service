package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.center.domain.enums.CenterStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InspectionCenterJpaRepository extends JpaRepository<InspectionCenterJpaEntity, UUID> {

    Optional<InspectionCenterJpaEntity> findByCenterIdAndDeletedAtIsNull(UUID centerId);

    Page<InspectionCenterJpaEntity> findAllByDeletedAtIsNull(Pageable pageable);

    long countByDeletedAtIsNull();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM InspectionCenterJpaEntity c WHERE c.centerId = :centerId AND c.deletedAt IS NULL")
    Optional<InspectionCenterJpaEntity> findByCenterIdForUpdate(@Param("centerId") UUID centerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c FROM InspectionCenterJpaEntity c
            WHERE c.status = :status AND c.currentLoad < c.capacity AND c.deletedAt IS NULL
            ORDER BY c.currentLoad ASC
            """)
    List<InspectionCenterJpaEntity> findAvailableForUpdate(@Param("status") CenterStatus status, Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM InspectionCenterJpaEntity c
            WHERE c.name = :name AND c.addressLine1 = :addressLine1 AND c.postalCode = :postalCode AND c.deletedAt IS NULL
            """)
    boolean existsByNameAndAddressLine1AndPostalCode(
            @Param("name") String name,
            @Param("addressLine1") String addressLine1,
            @Param("postalCode") String postalCode
    );

    @Modifying
    @Query("""
            UPDATE InspectionCenterJpaEntity c
            SET c.deletedAt = :deletedAt, c.deletedBy = :deletedBy
            WHERE c.centerId = :centerId AND c.deletedAt IS NULL
            """)
    void softDeleteById(
            @Param("centerId") UUID centerId,
            @Param("deletedAt") LocalDateTime deletedAt,
            @Param("deletedBy") String deletedBy
    );
}
