package com.coffeeMachine.service;

import com.coffeeMachine.dto.response.IngredientReplenishmentResponse;
import com.coffeeMachine.dto.request.IngredientsRequest;
import com.coffeeMachine.model.Ingredient;
import com.coffeeMachine.model.Recipe;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IngredientService {

    Ingredient findByName(String ingredientName);

    void saveIngredient(Ingredient ingredient);

    boolean validateOrder(Recipe recipe);

    List<IngredientReplenishmentResponse> replenishmentsIngredient(IngredientsRequest request);
}
