package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionRepository;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import com.trustamarket.inspectionservice.inspection.domain.vo.ProductId;
import com.trustamarket.inspectionservice.inspection.domain.vo.SellerId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InspectionRepositoryImpl implements InspectionRepository {

    private final InspectionJpaRepository jpaRepository;
    private final InspectionMapper mapper;

    @Override
    public Inspection save(Inspection inspection) {
        Optional<InspectionJpaEntity> existing = jpaRepository.findById(inspection.getId().value());
        if (existing.isPresent()) {
            mapper.updateJpaEntity(existing.get(), inspection);
            return mapper.toDomain(existing.get());
        }
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(inspection)));
    }

    @Override
    public Optional<Inspection> findById(InspectionId id) {
        return jpaRepository.findByInspectionIdAndDeletedAtIsNull(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Inspection> findByProductId(ProductId productId) {
        return jpaRepository.findByProductIdAndDeletedAtIsNull(productId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Inspection> findBySellerId(SellerId sellerId, int page, int size) {
        return jpaRepository.findBySellerIdAndDeletedAtIsNull(sellerId.value(), PageRequest.of(page, size))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countBySellerId(SellerId sellerId) {
        return jpaRepository.countBySellerIdAndDeletedAtIsNull(sellerId.value());
    }
}
