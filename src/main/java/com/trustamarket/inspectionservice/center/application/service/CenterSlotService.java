package com.trustamarket.inspectionservice.center.application.service;

import com.trustamarket.inspectionservice.center.application.dto.command.AssignCenterCommand;
import com.trustamarket.inspectionservice.center.application.dto.result.ReserveSlotResult;
import com.trustamarket.inspectionservice.center.application.event.SlotAssignedEvent;
import com.trustamarket.inspectionservice.center.application.port.in.AssignCenterForInspectionUseCase;
import com.trustamarket.inspectionservice.center.application.port.in.ReleaseSlotUseCase;
import com.trustamarket.inspectionservice.center.application.port.in.ReserveSlotUseCase;
import com.trustamarket.inspectionservice.center.application.port.out.InspectionCenterRepository;
import com.trustamarket.inspectionservice.center.application.port.out.SlotEventPublisher;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;
import com.trustamarket.inspectionservice.center.domain.model.InspectionCenter;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CenterSlotService implements ReserveSlotUseCase, ReleaseSlotUseCase, AssignCenterForInspectionUseCase {

    private final InspectionCenterRepository centerRepository;
    private final SlotEventPublisher slotEventPublisher;

    @Override
    @Transactional
    public ReserveSlotResult reserveSlot() {
        InspectionCenter center = centerRepository.findAvailableWithLock()
                .orElseThrow(() -> new InspectionCenterException("예약 가능한 검수 센터가 없습니다"));
        center.reserveSlot();
        InspectionCenter saved = centerRepository.save(center);
        return new ReserveSlotResult(saved.getId().value());
    }

    @Override
    @Transactional
    public void assign(AssignCenterCommand command) {
        InspectionCenter center = centerRepository.findAvailableWithLock()
                .orElseThrow(() -> new InspectionCenterException("예약 가능한 검수 센터가 없습니다"));
        center.reserveSlot();
        InspectionCenter saved = centerRepository.save(center);
        slotEventPublisher.publish(new SlotAssignedEvent(
                command.productId(), command.sellerId(), saved.getId().value(),
                command.originalPriceAmount(), command.currency()
        ));
    }

    @Override
    @Transactional
    public void releaseSlot(UUID centerId) {
        InspectionCenter center = centerRepository.findByIdWithLock(CenterId.of(centerId))
                .orElseThrow(() -> new InspectionCenterException("존재하지 않는 센터입니다: " + centerId));
        center.releaseSlot();
        centerRepository.save(center);
    }
}
