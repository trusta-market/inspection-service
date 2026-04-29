package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InspectionCenterJpaRepository extends JpaRepository<InspectionCenterJpaEntity, UUID> {

    Optional<InspectionCenterJpaEntity> findByCenterIdAndDeletedAtIsNull(UUID centerId);

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
}
