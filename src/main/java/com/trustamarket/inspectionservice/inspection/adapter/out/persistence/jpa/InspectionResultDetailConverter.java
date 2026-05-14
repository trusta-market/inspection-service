package com.trustamarket.inspectionservice.inspection.adapter.out.persistence.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.inspectionservice.inspection.domain.vo.InspectionResultDetail;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class InspectionResultDetailConverter implements AttributeConverter<InspectionResultDetail, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(InspectionResultDetail attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(attribute.data());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("InspectionResultDetail 직렬화 실패", e);
        }
    }

    @Override
    public InspectionResultDetail convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return InspectionResultDetail.empty();
        }
        try {
            Map<String, Object> data = MAPPER.readValue(dbData, new TypeReference<>() {});
            return new InspectionResultDetail(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("InspectionResultDetail 역직렬화 실패", e);
        }
    }
}
