package com.trustamarket.inspectionservice.center.application.service;

import com.trustamarket.inspectionservice.center.application.dto.command.RegisterCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.result.RegisterCenterResult;
import com.trustamarket.inspectionservice.center.application.port.in.RegisterCenterUseCase;
import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.Address;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InspectionCenterService implements RegisterCenterUseCase {

    private final InspectionCenterRepository inspectionCenterRepository;

    @Override
    @Transactional
    public RegisterCenterResult register(RegisterCenterCommand command) {
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
                command.contactPhone(),
                command.capacity()
        );

        InspectionCenter saved = inspectionCenterRepository.save(center);

        return new RegisterCenterResult(
                saved.getId().value(),
                saved.getName(),
                saved.getStatus()
        );
    }
}
