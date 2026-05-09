package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.center.domain.enums.CenterStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_inspection_centers")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InspectionCenterJpaEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID centerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    private String contactPhone;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int currentLoad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CenterStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    @Column
    private String deletedBy;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return centerId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    public void softDelete(Instant deletedAt, String deletedBy) {
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }

    public void update(
            String name,
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String contactPhone,
            int capacity,
            int currentLoad,
            CenterStatus status
    ) {
        this.name = name;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.contactPhone = contactPhone;
        this.capacity = capacity;
        this.currentLoad = currentLoad;
        this.status = status;
    }

    public static InspectionCenterJpaEntity of(
            UUID id,
            String name,
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String contactPhone,
            int capacity,
            int currentLoad,
            CenterStatus status
    ) {
        InspectionCenterJpaEntity entity = new InspectionCenterJpaEntity();
        entity.centerId = id;
        entity.name = name;
        entity.addressLine1 = addressLine1;
        entity.addressLine2 = addressLine2;
        entity.city = city;
        entity.postalCode = postalCode;
        entity.contactPhone = contactPhone;
        entity.capacity = capacity;
        entity.currentLoad = currentLoad;
        entity.status = status;
        return entity;
    }
}
