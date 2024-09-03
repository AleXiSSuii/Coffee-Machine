package com.coffeeMachine.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.persistence.AttributeConverter;

import java.util.HashMap;
import java.util.Map;


public class IngredientMapConverter implements AttributeConverter<Map<String,Integer>,String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Integer> ingredients) {
        return new Gson().toJson(ingredients);

    }

    @Override
    public Map<String, Integer> convertToEntityAttribute(String ingredientsJson) {
        Map<String,Integer> result = null;
        try {
            result = new ObjectMapper().readValue(ingredientsJson, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
