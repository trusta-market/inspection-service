package com.trustamarket.inspectionservice.inspection.application.service;

import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.CompleteReturnCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.FailInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.MarkArrivedCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.RequestInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.command.StartInspectionCommand;
import com.trustamarket.inspectionservice.inspection.application.dto.query.GetMyInspectionsQuery;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionPageResult;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionResult;
import com.trustamarket.inspectionservice.inspection.application.dto.result.GetInspectionSummaryResult;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionCompletedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionFailedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionReturnCompletedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.InspectionStartedEvent;
import com.trustamarket.inspectionservice.inspection.application.event.PricingCompletedEvent;
import com.trustamarket.inspectionservice.inspection.application.port.in.CompleteInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.CompleteReturnUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.FailInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.GetInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.MarkArrivedUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.RequestInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.in.StartInspectionUseCase;
import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionEventPublisher;
import com.trustamarket.inspectionservice.inspection.application.port.out.InspectionRepository;
import com.trustamarket.inspectionservice.inspection.domain.enums.CurrencyCode;
import com.trustamarket.inspectionservice.inspection.domain.enums.Grade;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;
import com.trustamarket.inspectionservice.inspection.domain.model.Inspection;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionResultDetail;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectorId;
import com.trustamarket.inspectionservice.inspection.domain.vo.Money;
import com.trustamarket.inspectionservice.inspection.domain.vo.ProductId;
import com.trustamarket.inspectionservice.inspection.domain.vo.SellerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InspectionService implements RequestInspectionUseCase, MarkArrivedUseCase, StartInspectionUseCase,
        CompleteInspectionUseCase, FailInspectionUseCase, CompleteReturnUseCase, GetInspectionUseCase {

    private final InspectionRepository inspectionRepository;
    private final InspectionEventPublisher inspectionEventPublisher;

    @Override
    @Transactional
    public void request(RequestInspectionCommand command) {
        Inspection inspection = Inspection.request(
                InspectionId.generate(),
                ProductId.of(command.productId()),
                SellerId.of(command.sellerId()),
                CenterId.of(command.centerId()),
                Money.of(command.originalPriceAmount(), CurrencyCode.valueOf(command.currency())),
                Instant.now()
        );
        inspectionRepository.save(inspection);
    }

    @Override
    @Transactional
    public void markArrived(MarkArrivedCommand command) {
        Inspection inspection = inspectionRepository.findByProductId(ProductId.of(command.productId()))
                .orElseThrow(() -> new InspectionException(InspectionErrorCode.INSPECTION_NOT_FOUND, "productId=" + command.productId()));
        inspection.markArrived(Instant.now());
        inspectionRepository.save(inspection);
    }

    @Override
    @Transactional
    public void complete(CompleteInspectionCommand command) {
        Inspection inspection = inspectionRepository.findById(InspectionId.of(command.inspectionId()))
                .orElseThrow(() -> new InspectionException(InspectionErrorCode.INSPECTION_NOT_FOUND, "inspectionId=" + command.inspectionId()));
        inspection.completeInspection(
                Grade.valueOf(command.grade()),
                Money.of(command.suggestedPriceAmount(), CurrencyCode.valueOf(command.currency())),
                command.inspectorNote(),
                command.resultDetail() != null ? new InspectionResultDetail(command.resultDetail()) : InspectionResultDetail.empty(),
                Instant.now()
        );
        inspectionRepository.save(inspection);
        inspectionEventPublisher.publish(new InspectionCompletedEvent(
                inspection.getId().value(),
                inspection.getProductId().value()
        ));
        inspectionEventPublisher.publish(new PricingCompletedEvent(
                inspection.getId().value(),
                inspection.getProductId().value(),
                inspection.getGrade().name(),
                inspection.getSuggestedPrice().amount().longValue(),
                inspection.getSuggestedPrice().currency().name()
        ));
    }

    @Override
    @Transactional
    public void fail(FailInspectionCommand command) {
        Inspection inspection = inspectionRepository.findById(InspectionId.of(command.inspectionId()))
                .orElseThrow(() -> new InspectionException(InspectionErrorCode.INSPECTION_NOT_FOUND, "inspectionId=" + command.inspectionId()));
        inspection.failInspection(
                command.inspectorNote(),
                command.resultDetail() != null ? new InspectionResultDetail(command.resultDetail()) : InspectionResultDetail.empty(),
                Instant.now()
        );
        inspectionRepository.save(inspection);
        inspectionEventPublisher.publish(new InspectionFailedEvent(
                inspection.getId().value(),
                inspection.getProductId().value(),
                inspection.getSellerId().value()
        ));
    }

    @Override
    @Transactional
    public void start(StartInspectionCommand command) {
        Inspection inspection = inspectionRepository.findById(InspectionId.of(command.inspectionId()))
                .orElseThrow(() -> new InspectionException(InspectionErrorCode.INSPECTION_NOT_FOUND, "inspectionId=" + command.inspectionId()));
        inspection.start(InspectorId.of(command.inspectorId()), Instant.now());
        inspectionRepository.save(inspection);
        inspectionEventPublisher.publish(new InspectionStartedEvent(
                inspection.getId().value(),
                inspection.getProductId().value()
        ));
    }

    @Override
    @Transactional
    public void completeReturn(CompleteReturnCommand command) {
        Inspection inspection = inspectionRepository.findByProductId(ProductId.of(command.productId()))
                .orElseThrow(() -> new InspectionException(InspectionErrorCode.INSPECTION_NOT_FOUND, "productId=" + command.productId()));
        inspection.completeReturn(Instant.now());
        inspectionRepository.save(inspection);
        inspectionEventPublisher.publish(new InspectionReturnCompletedEvent(
                inspection.getId().value(),
                inspection.getProductId().value(),
                inspection.getSellerId().value()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public GetInspectionPageResult getMyInspections(GetMyInspectionsQuery query) {
        SellerId sellerId = SellerId.of(query.userId());
        List<Inspection> inspections = inspectionRepository.findBySellerId(sellerId, query.page(), query.size());
        long total = inspectionRepository.countBySellerId(sellerId);
        int totalPages = (int) Math.ceil((double) total / query.size());

        List<GetInspectionSummaryResult> content = inspections.stream()
                .map(GetInspectionSummaryResult::from)
                .toList();

        return new GetInspectionPageResult(content, query.page(), query.size(), total, totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public GetInspectionResult getInspection(UUID inspectionId) {
        Inspection inspection = inspectionRepository.findById(InspectionId.of(inspectionId))
                .orElseThrow(() -> new InspectionException(InspectionErrorCode.INSPECTION_NOT_FOUND, "inspectionId=" + inspectionId));
        return GetInspectionResult.from(inspection);
    }
}
