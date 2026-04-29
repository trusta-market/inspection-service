package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionRepository;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InspectionRepositoryImpl implements InspectionRepository {

    private final InspectionJpaRepository jpaRepository;
    private final InspectionMapper mapper;

    @Override
    public Inspection save(Inspection inspection) {
        InspectionJpaEntity entity = jpaRepository.findByInspectionIdAndDeletedAtIsNull(inspection.getId().value())
                .map(existing -> mapper.updateJpaEntity(existing, inspection))
                .orElseGet(() -> mapper.toJpaEntity(inspection));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Inspection> findById(InspectionId id) {
        return jpaRepository.findByInspectionIdAndDeletedAtIsNull(id.value()).map(mapper::toDomain);
    }
}
