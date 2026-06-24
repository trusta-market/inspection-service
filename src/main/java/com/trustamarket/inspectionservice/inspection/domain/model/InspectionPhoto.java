package com.trustamarket.inspectionservice.inspection.domain.model;

import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionErrorCode;
import com.trustamarket.inspectionservice.inspection.domain.exception.InspectionException;
import com.trustamarket.inspectionservice.inspection.domain.enums.PhotoType;
import com.trustamarket.inspectionservice.inspection.domain.vo.PhotoId;

import java.util.Objects;

public record InspectionPhoto(
        PhotoId id,
        PhotoType type,
        String url,
        String caption,
        int displayOrder
) {
    public InspectionPhoto {
        Objects.requireNonNull(id, "PhotoId는 필수입니다");
        Objects.requireNonNull(type, "PhotoType은 필수입니다");

        if (url == null || url.isBlank()) {
            throw new InspectionException(InspectionErrorCode.INVALID_PHOTO_URL);
        }

        if (displayOrder < 0) {
            throw new InspectionException(InspectionErrorCode.INVALID_DISPLAY_ORDER);
        }
    }

        public static InspectionPhoto of(
                PhotoId id,
                PhotoType type,
                String url,
                String caption,
                int displayOrder
        ) {
            return new InspectionPhoto(id, type, url, caption, displayOrder);
        }
}
