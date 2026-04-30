package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.inspection.domain.enums.CurrencyCode;
import com.trustamarket.inspectionservice.inspection.domain.enums.Grade;
import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;

import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionResultDetail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_inspections")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InspectionJpaEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID inspectionId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID sellerId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID centerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal originalPriceAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode originalPriceCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionStatus status;

    @Column(columnDefinition = "uuid")
    private UUID inspectorId;

    @Column(nullable = false)
    private Instant requestedAt;

    private Instant arrivedAt;
    private Instant startedAt;
    private Instant inspectionDoneAt;
    private Instant pricedAt;
    private Instant returnCompletedAt;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(precision = 19, scale = 2)
    private BigDecimal suggestedPriceAmount;

    @Enumerated(EnumType.STRING)
    private CurrencyCode suggestedPriceCurrency;

    @Column(columnDefinition = "text")
    private String inspectorNote;

    @Convert(converter = InspectionResultDetailConverter.class)
    @Column(columnDefinition = "text")
    private InspectionResultDetail resultDetail;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private List<InspectionPhotoJpaEntity> photos = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
    private String deletedBy;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return inspectionId;
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

    public void update(
            InspectionStatus status,
            UUID inspectorId,
            Instant arrivedAt,
            Instant startedAt,
            Instant inspectionDoneAt,
            Instant pricedAt,
            Instant returnCompletedAt,
            Grade grade,
            BigDecimal suggestedPriceAmount,
            CurrencyCode suggestedPriceCurrency,
            String inspectorNote,
            InspectionResultDetail resultDetail
    ) {
        this.status = status;
        this.inspectorId = inspectorId;
        this.arrivedAt = arrivedAt;
        this.startedAt = startedAt;
        this.inspectionDoneAt = inspectionDoneAt;
        this.pricedAt = pricedAt;
        this.returnCompletedAt = returnCompletedAt;
        this.grade = grade;
        this.suggestedPriceAmount = suggestedPriceAmount;
        this.suggestedPriceCurrency = suggestedPriceCurrency;
        this.inspectorNote = inspectorNote;
        this.resultDetail = resultDetail;
    }

    // 추가된 사진만 INSERT, 제거된 사진만 DELETE — 변경 없는 사진은 그대로 유지
    public void syncPhotos(List<InspectionPhotoJpaEntity> domainPhotos) {
        photos.removeIf(existing ->
                domainPhotos.stream().noneMatch(dp -> dp.getPhotoId().equals(existing.getPhotoId()))
        );
        domainPhotos.forEach(dp -> {
            if (photos.stream().noneMatch(existing -> existing.getPhotoId().equals(dp.getPhotoId()))) {
                photos.add(dp);
            }
        });
    }

    public static InspectionJpaEntity of(
            UUID inspectionId,
            UUID productId,
            UUID sellerId,
            UUID centerId,
            BigDecimal originalPriceAmount,
            CurrencyCode originalPriceCurrency,
            InspectionStatus status,
            Instant requestedAt
    ) {
        InspectionJpaEntity entity = new InspectionJpaEntity();
        entity.inspectionId = inspectionId;
        entity.productId = productId;
        entity.sellerId = sellerId;
        entity.centerId = centerId;
        entity.originalPriceAmount = originalPriceAmount;
        entity.originalPriceCurrency = originalPriceCurrency;
        entity.status = status;
        entity.requestedAt = requestedAt;
        return entity;
    }
}
