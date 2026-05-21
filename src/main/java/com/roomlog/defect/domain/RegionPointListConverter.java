package com.roomlog.defect.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomlog.defect.dto.RegionPoint;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class RegionPointListConverter implements AttributeConverter<List<RegionPoint>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RegionPoint> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<RegionPoint> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<RegionPoint>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
