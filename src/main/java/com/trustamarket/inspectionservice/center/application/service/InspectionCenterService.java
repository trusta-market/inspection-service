package com.trustamarket.inspectionservice.center.application.service;

import com.trustamarket.inspectionservice.center.application.dto.command.RegisterCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.command.UpdateCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.query.GetCentersQuery;
import com.trustamarket.inspectionservice.center.application.dto.result.ChangeCenterStatusResult;
import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterPageResult;
import com.trustamarket.inspectionservice.center.application.dto.result.GetCenterResult;
import com.trustamarket.inspectionservice.center.application.dto.result.RegisterCenterResult;
import com.trustamarket.inspectionservice.center.application.dto.result.UpdateCenterResult;
import com.trustamarket.inspectionservice.center.application.port.in.InspectionCenterUseCase;
import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterErrorCode;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.Address;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InspectionCenterService implements InspectionCenterUseCase {

    private final InspectionCenterRepository inspectionCenterRepository;

    @Override
    @Transactional
    public RegisterCenterResult register(RegisterCenterCommand command) {
        if (inspectionCenterRepository.existsByNameAndAddress(command.name(), command.addressLine1(), command.postalCode())) {
            throw new InspectionCenterException(InspectionCenterErrorCode.DUPLICATE_CENTER);
        }

        Address address = new Address(
                command.addressLine1(),
                command.addressLine2(),
                command.city(),
                command.postalCode()
        );

        InspectionCenter center = InspectionCenter.register(
                CenterId.generate(),
                command.name(),
                address,
                command.contactPhone()
        );

        InspectionCenter saved = inspectionCenterRepository.save(center);

        return new RegisterCenterResult(
                saved.getId().value(),
                saved.getName(),
                saved.getStatus()
        );
    }

    @Override
    @Transactional
    public UpdateCenterResult updateCenter(UpdateCenterCommand command) {
        InspectionCenter center = findCenterOrThrow(command.centerId());
        center.rename(command.name());
        center.relocate(new Address(command.addressLine1(), command.addressLine2(), command.city(), command.postalCode()));
        center.updateContactPhone(command.contactPhone());
        InspectionCenter saved = inspectionCenterRepository.save(center);
        return UpdateCenterResult.from(saved);
    }

    @Override
    @Transactional
    public ChangeCenterStatusResult open(UUID centerId) {
        InspectionCenter center = findCenterOrThrow(centerId);
        center.open();
        InspectionCenter saved = inspectionCenterRepository.save(center);
        return new ChangeCenterStatusResult(saved.getId().value(), saved.getStatus());
    }

    @Override
    @Transactional
    public ChangeCenterStatusResult startMaintenance(UUID centerId) {
        InspectionCenter center = findCenterOrThrow(centerId);
        center.startMaintenance();
        InspectionCenter saved = inspectionCenterRepository.save(center);
        return new ChangeCenterStatusResult(saved.getId().value(), saved.getStatus());
    }

    @Override
    @Transactional
    public ChangeCenterStatusResult close(UUID centerId) {
        InspectionCenter center = findCenterOrThrow(centerId);
        center.close();
        InspectionCenter saved = inspectionCenterRepository.save(center);
        return new ChangeCenterStatusResult(saved.getId().value(), saved.getStatus());
    }

    @Override
    @Transactional
    public void delete(UUID centerId, String deletedBy) {
        InspectionCenter center = findCenterOrThrow(centerId);
        center.validateDeletable();
        inspectionCenterRepository.delete(CenterId.of(centerId), deletedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCenterResult getCenter(UUID centerId) {
        return GetCenterResult.from(findCenterOrThrow(centerId));
    }

    @Override
    @Transactional(readOnly = true)
    public GetCenterPageResult getCenters(GetCentersQuery query) {
        List<GetCenterResult> content = inspectionCenterRepository.findAll(query).stream()
                .map(GetCenterResult::from)
                .toList();
        long totalElements = inspectionCenterRepository.countAll();
        int totalPages = (int) Math.ceil((double) totalElements / query.size());
        return new GetCenterPageResult(content, query.page(), query.size(), totalElements, totalPages);
    }

    private InspectionCenter findCenterOrThrow(UUID centerId) {
        return inspectionCenterRepository.findById(CenterId.of(centerId))
                .orElseThrow(() -> new InspectionCenterException(InspectionCenterErrorCode.CENTER_NOT_FOUND, centerId.toString()));
    }
}
