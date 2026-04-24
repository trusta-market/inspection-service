package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InspectionCenterRepositoryImpl implements InspectionCenterRepository {

    private final InspectionCenterJpaRepository jpaRepository;
    private final InspectionCenterMapper mapper;

    @Override
    public InspectionCenter save(InspectionCenter center) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(center)));
    }

    @Override
    public Optional<InspectionCenter> findById(CenterId id) {
        return jpaRepository.findByCenterIdAndDeletedAtIsNull(id.value())
                .map(mapper::toDomain);
    }
}
