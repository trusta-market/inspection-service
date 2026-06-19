package com.trustamarket.inspectionservice.inspection.domain.model;

import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import com.trustamarket.inspectionservice.inspection.domain.enums.Grade;
import com.trustamarket.inspectionservice.inspection.domain.enums.InspectionStatus;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionId;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionResultDetail;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectorId;
import com.trustamarket.inspectionservice.inspection.domain.vo.Money;
import com.trustamarket.inspectionservice.inspection.domain.vo.ProductId;
import com.trustamarket.inspectionservice.inspection.domain.vo.SellerId;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class Inspection {

    private final InspectionId id;
    private final ProductId productId;
    private final SellerId sellerId;
    private final CenterId centerId;

    private final Money originalPrice;
    private final Instant requestedAt;

    private InspectionStatus status;
    private InspectorId inspectorId;
    private Instant arrivedAt;
    private Instant startedAt;
    private Instant inspectionDoneAt;
    private Instant pricedAt;
    private Instant returnCompletedAt;

    private Grade grade;
    private Money suggestedPrice;
    private String inspectorNote;
    private InspectionResultDetail resultDetail;

    private final List<InspectionPhoto> photos = new ArrayList<>();

    private Inspection(
            InspectionId id,
            ProductId productId,
            SellerId sellerId,
            CenterId centerId,
            Money originalPrice,
            Instant requestedAt,
            InspectionStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.productId = Objects.requireNonNull(productId);
        this.sellerId = Objects.requireNonNull(sellerId);
        this.centerId = Objects.requireNonNull(centerId);
        this.originalPrice = Objects.requireNonNull(originalPrice);
        this.requestedAt = Objects.requireNonNull(requestedAt);
        this.status = Objects.requireNonNull(status);
    }

    private Inspection(
            InspectionId id,
            ProductId productId,
            SellerId sellerId,
            CenterId centerId,
            Money originalPrice,
            Instant requestedAt,
            InspectionStatus status,
            InspectorId inspectorId,
            Instant arrivedAt,
            Instant startedAt,
            Instant inspectionDoneAt,
            Instant pricedAt,
            Instant returnCompletedAt,
            Grade grade,
            Money suggestedPrice,
            String inspectorNote,
            InspectionResultDetail resultDetail,
            List<InspectionPhoto> photos
    ) {
        this(id, productId, sellerId, centerId, originalPrice, requestedAt, status);
        this.inspectorId = inspectorId;
        this.arrivedAt = arrivedAt;
        this.startedAt = startedAt;
        this.inspectionDoneAt = inspectionDoneAt;
        this.pricedAt = pricedAt;
        this.returnCompletedAt = returnCompletedAt;
        this.grade = grade;
        this.suggestedPrice = suggestedPrice;
        this.inspectorNote = inspectorNote;
        this.resultDetail = resultDetail;
        if (photos != null) {
            this.photos.addAll(photos);
        }
    }

    public static Inspection request(
            InspectionId id,
            ProductId productId,
            SellerId sellerId,
            CenterId centerId,
            Money originalPrice,
            Instant requestedAt
    ) {
        return new Inspection(
                id, productId, sellerId, centerId, originalPrice, requestedAt,
                InspectionStatus.REQUESTED
        );
    }

    public void markArrived(Instant arrivedAt) {
        requireStatus(InspectionStatus.REQUESTED, "입고 확인");
        this.status = InspectionStatus.ARRIVED;
        this.arrivedAt = Objects.requireNonNull(arrivedAt);
    }

    public void start(InspectorId inspectorId, Instant startedAt) {
        requireStatus(InspectionStatus.ARRIVED, "검수 시작");
        this.status = InspectionStatus.IN_PROGRESS;
        this.inspectorId = Objects.requireNonNull(inspectorId);
        this.startedAt = Objects.requireNonNull(startedAt);
    }

    public void completeInspection(
            Grade grade,
            Money suggestedPrice,
            String inspectorNote,
            InspectionResultDetail resultDetail,
            Instant at
    ) {
        requireStatus(InspectionStatus.IN_PROGRESS, "검수 완료(통과)");
        this.grade = Objects.requireNonNull(grade, "grade는 필수입니다");
        this.suggestedPrice = Objects.requireNonNull(suggestedPrice, "suggestedPrice는 필수입니다");
        this.inspectorNote = inspectorNote;
        this.resultDetail = (resultDetail == null) ? InspectionResultDetail.empty() : resultDetail;
        this.inspectionDoneAt = Objects.requireNonNull(at);
        this.pricedAt = at;
        this.status = InspectionStatus.PRICED;
    }

    public void failInspection(
            String inspectorNote,
            InspectionResultDetail resultDetail,
            Instant at
    ) {
        requireStatus(InspectionStatus.IN_PROGRESS, "검수 실패 처리");
        if (inspectorNote == null || inspectorNote.isBlank()) {
            throw new InspectionException(InspectionErrorCode.INSPECTOR_NOTE_REQUIRED);
        }
        this.inspectorNote = inspectorNote;
        this.resultDetail = (resultDetail == null) ? InspectionResultDetail.empty() : resultDetail;
        this.inspectionDoneAt = Objects.requireNonNull(at);
        this.status = InspectionStatus.FAILED;
    }

    public void acceptPrice(Instant at) {
        requireStatus(InspectionStatus.PRICED, "가격 수락");
        this.status = InspectionStatus.PRICE_ACCEPTED;
    }

    public void rejectPrice(Instant at) {
        requireStatus(InspectionStatus.PRICED, "가격 거절");
        this.status = InspectionStatus.PRICE_REJECTED;
    }

    public void completeReturn(Instant at) {
        if (this.status != InspectionStatus.FAILED && this.status != InspectionStatus.PRICE_REJECTED) {
            throw new InspectionException(
                    InspectionErrorCode.INVALID_STATUS_TRANSITION, "반송 완료: 현재 상태=" + this.status
            );
        }
        this.returnCompletedAt = Objects.requireNonNull(at);
        this.status = InspectionStatus.RETURN_COMPLETED;
    }

    public List<InspectionPhoto> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    private void requireStatus(InspectionStatus expected, String action) {
        if (this.status != expected) {
            throw new InspectionException(
                    InspectionErrorCode.INVALID_STATUS_TRANSITION, action + ": " + expected + " → " + this.status
            );
        }
    }

    public static Inspection restore(
            InspectionId id,
            ProductId productId,
            SellerId sellerId,
            CenterId centerId,
            Money originalPrice,
            Instant requestedAt,
            InspectionStatus status,
            InspectorId inspectorId,
            Instant arrivedAt,
            Instant startedAt,
            Instant inspectionDoneAt,
            Instant pricedAt,
            Instant returnCompletedAt,
            Grade grade,
            Money suggestedPrice,
            String inspectorNote,
            InspectionResultDetail resultDetail,
            List<InspectionPhoto> photos
    ) {
        return new Inspection(
                id, productId, sellerId, centerId, originalPrice, requestedAt, status,
                inspectorId, arrivedAt, startedAt, inspectionDoneAt, pricedAt,
                returnCompletedAt, grade, suggestedPrice, inspectorNote,
                resultDetail, photos
        );
    }
}
