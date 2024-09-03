package com.coffeeMachine.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecipeResponse {
    private boolean isSuccess;
    private String nameRecipe;
    private String message;
}
