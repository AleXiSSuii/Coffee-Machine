package com.coffeeMachine.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RecipeRequest {
    private String name;
    @NotNull
    private Map<String, Integer> ingredients;
    @Min(1)
    private Integer preparationTime;
}
