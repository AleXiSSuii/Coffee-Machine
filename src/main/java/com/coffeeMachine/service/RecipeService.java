package com.coffeeMachine.service;

import com.coffeeMachine.dto.request.RecipeRequest;
import com.coffeeMachine.dto.response.RecipeResponse;
import com.coffeeMachine.model.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe getRecipeByName(String name);

    List<Recipe> getAllRecipes();

    RecipeResponse addNewRecipe(RecipeRequest request);

    RecipeResponse getMostPopularRecipe();
}
