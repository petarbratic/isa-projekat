package rs.ac.ftn.isa.backend.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter
public class StringLongMapJsonConverter implements AttributeConverter<Map<String, Long>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Long>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, Long> attribute) {
        try {
            if (attribute == null) return "{}";
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize counts map to JSON.", e);
        }
    }

    @Override
    public Map<String, Long> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) return new HashMap<>();
            return MAPPER.readValue(dbData, TYPE);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize counts JSON to map.", e);
        }
    }
}