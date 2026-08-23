package com.roomlog.defect.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomlog.defect.dto.RepairItem;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class RepairItemListConverter implements AttributeConverter<List<RepairItem>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RepairItem> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<RepairItem> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<RepairItem>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
