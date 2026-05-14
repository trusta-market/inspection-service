package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.inspection.domain.enums.PhotoType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_inspection_photos")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InspectionPhotoJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID photoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhotoType type;

    @Column(nullable = false)
    private String url;

    private String caption;

    @Column(nullable = false)
    private int displayOrder;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static InspectionPhotoJpaEntity of(UUID photoId, PhotoType type, String url, String caption, int displayOrder) {
        InspectionPhotoJpaEntity entity = new InspectionPhotoJpaEntity();
        entity.photoId = photoId;
        entity.type = type;
        entity.url = url;
        entity.caption = caption;
        entity.displayOrder = displayOrder;
        return entity;
    }
}
