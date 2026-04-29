package com.trustamarket.inspectionservice.center.adapter.out.persistence.jpa;

import com.trustamarket.inspectionservice.center.application.dto.query.GetCentersQuery;
import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.domain.enums.CenterStatus;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
    public Optional<InspectionCenter> findByIdWithLock(CenterId id) {
        return jpaRepository.findByCenterIdForUpdate(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<InspectionCenter> findAvailableWithLock() {
        return jpaRepository.findAvailableForUpdate(CenterStatus.OPEN, PageRequest.of(0, 1))
                .stream().findFirst()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByNameAndAddress(String name, String addressLine1, String postalCode) {
        return jpaRepository.existsByNameAndAddressLine1AndPostalCode(name, addressLine1, postalCode);
    }

    @Override
    public void delete(CenterId id, String deletedBy) {
        jpaRepository.softDeleteById(id.value(), LocalDateTime.now(), deletedBy);
    }

    @Override
    public List<InspectionCenter> findAll(GetCentersQuery query) {
        return jpaRepository.findAllByDeletedAtIsNull(PageRequest.of(query.page(), query.size()))
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countAll() {
        return jpaRepository.countByDeletedAtIsNull();
    }
}
