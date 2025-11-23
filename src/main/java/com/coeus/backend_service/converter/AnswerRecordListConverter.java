/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.converter;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.PlayerStatus.AnswerRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Converts a List<AnswerRecord> to a JSON String for storage in a single database column.
 * This resolves the "nested ElementCollection" Hibernate error.
 */
@Converter
public class AnswerRecordListConverter implements AttributeConverter<List<AnswerRecord>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeReference<List<AnswerRecord>> typeRef = new TypeReference<List<AnswerRecord>>() {};

    /**
     * Converts the list of AnswerRecord objects into a JSON string for persistence.
     */
    @Override
    public String convertToDatabaseColumn(List<AnswerRecord> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // In a real application, you might want more robust logging or error handling
            System.err.println("Error converting AnswerRecord list to JSON: " + e.getMessage());
            return null; // Return null on failure to prevent app crash
        }
    }

    /**
     * Converts the JSON string from the database back into a List of AnswerRecord objects.
     */
    @Override
    public List<AnswerRecord> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, typeRef);
        } catch (IOException e) {
            System.err.println("Error converting JSON to AnswerRecord list: " + e.getMessage());
            return new ArrayList<>(); // Return empty list on failure
        }
    }
}
