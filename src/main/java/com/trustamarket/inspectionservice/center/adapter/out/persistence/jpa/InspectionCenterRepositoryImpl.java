package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InspectionCenterRepositoryImpl implements InspectionCenterRepository {

    private final InspectionCenterJpaRepository jpaRepository;
    private final InspectionCenterMapper mapper;

    @Override
    public InspectionCenter save(InspectionCenter center) {
        UUID id = center.getId().value();
        InspectionCenterJpaEntity entity = jpaRepository.findById(id)
                .map(existing -> mapper.updateJpaEntity(existing, center))
                .orElseGet(() -> mapper.toJpaEntity(center));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<InspectionCenter> findById(CenterId id) {
        return jpaRepository.findByCenterIdAndDeletedAtIsNull(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByNameAndAddress(String name, String addressLine1, String postalCode) {
        return jpaRepository.existsByNameAndAddressLine1AndPostalCode(name, addressLine1, postalCode);
    }
}
