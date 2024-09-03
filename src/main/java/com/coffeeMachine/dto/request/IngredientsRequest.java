package com.coffeeMachine.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class IngredientsRequest {
    @NotNull
    private Map<String, Integer> ingredients;
}
