package com.trustamarket.inspectionservice.inspection.domain.model.vo;

import java.util.Map;

public record InspectionResultDetail(Map<String, Object> data) {

    public InspectionResultDetail {
        data = (data == null) ? Map.of() : Map.copyOf(data);
    }

    public static InspectionResultDetail empty() {
        return new InspectionResultDetail(Map.of());
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
