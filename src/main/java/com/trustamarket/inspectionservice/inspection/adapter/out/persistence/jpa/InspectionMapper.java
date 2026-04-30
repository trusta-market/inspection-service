package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.model.InspectionPhoto;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectorId;
import com.trustamarket.inspectionservice.inspection.domain.vo.Money;
import com.trustamarket.inspectionservice.inspection.domain.vo.PhotoId;
import com.trustamarket.inspectionservice.inspection.domain.vo.ProductId;
import com.trustamarket.inspectionservice.inspection.domain.vo.SellerId;
import org.springframework.stereotype.Component;

@Component
public class InspectionMapper {

    public InspectionJpaEntity toJpaEntity(Inspection inspection) {
        return InspectionJpaEntity.of(
                inspection.getId().value(),
                inspection.getProductId().value(),
                inspection.getSellerId().value(),
                inspection.getCenterId().value(),
                inspection.getOriginalPrice().amount(),
                inspection.getOriginalPrice().currency(),
                inspection.getStatus(),
                inspection.getRequestedAt()
        );
    }

    public InspectionJpaEntity updateJpaEntity(InspectionJpaEntity existing, Inspection inspection) {
        existing.update(
                inspection.getStatus(),
                inspection.getInspectorId() != null ? inspection.getInspectorId().value() : null,
                inspection.getArrivedAt(),
                inspection.getStartedAt(),
                inspection.getInspectionDoneAt(),
                inspection.getPricedAt(),
                inspection.getReturnCompletedAt(),
                inspection.getGrade(),
                inspection.getSuggestedPrice() != null ? inspection.getSuggestedPrice().amount() : null,
                inspection.getSuggestedPrice() != null ? inspection.getSuggestedPrice().currency() : null,
                inspection.getInspectorNote(),
                inspection.getResultDetail()
        );
        existing.syncPhotos(
                inspection.getPhotos().stream()
                        .map(p -> InspectionPhotoJpaEntity.of(p.id().value(), p.type(), p.url(), p.caption(), p.displayOrder()))
                        .toList()
        );
        return existing;
    }

    public Inspection toDomain(InspectionJpaEntity entity) {
        return Inspection.restore(
                InspectionId.of(entity.getInspectionId()),
                ProductId.of(entity.getProductId()),
                SellerId.of(entity.getSellerId()),
                CenterId.of(entity.getCenterId()),
                Money.of(entity.getOriginalPriceAmount(), entity.getOriginalPriceCurrency()),
                entity.getRequestedAt(),
                entity.getStatus(),
                entity.getInspectorId() != null ? InspectorId.of(entity.getInspectorId()) : null,
                entity.getArrivedAt(),
                entity.getStartedAt(),
                entity.getInspectionDoneAt(),
                entity.getPricedAt(),
                entity.getReturnCompletedAt(),
                entity.getGrade(),
                entity.getSuggestedPriceAmount() != null
                        ? Money.of(entity.getSuggestedPriceAmount(), entity.getSuggestedPriceCurrency()) : null,
                entity.getInspectorNote(),
                entity.getResultDetail(),
                entity.getPhotos().stream()
                        .map(p -> InspectionPhoto.of(PhotoId.of(p.getPhotoId()), p.getType(), p.getUrl(), p.getCaption(), p.getDisplayOrder()))
                        .toList()
        );
    }
}
