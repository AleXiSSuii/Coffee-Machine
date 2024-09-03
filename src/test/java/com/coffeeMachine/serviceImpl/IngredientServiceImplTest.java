package com.coffeeMachine.serviceImpl;

import com.coffeeMachine.dto.request.IngredientsRequest;
import com.coffeeMachine.dto.response.IngredientReplenishmentResponse;
import com.coffeeMachine.model.Ingredient;
import com.coffeeMachine.model.Recipe;
import com.coffeeMachine.repo.IngredientsRepo;
import com.coffeeMachine.util.InsufficientIngredientsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private  IngredientsRepo ingredientsRepo;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @Test
    void testFindByNameSuccess() {
        String ingredientName = "молоко";
        Ingredient expectedIngredient = new Ingredient(1L,"молоко",1000,1500);

        when(ingredientsRepo.findByName(ingredientName)).thenReturn(Optional.of(expectedIngredient));

        Ingredient actualIngredient = ingredientService.findByName(ingredientName);
        assertEquals(expectedIngredient, actualIngredient);
    }
    @Test
    void testFindByNameIngredientNotFound() {
        String ingredientName = "NonexistentIngredient";
        when(ingredientsRepo.findByName(ingredientName)).thenReturn(Optional.empty());

        assertThrows(InsufficientIngredientsException.class, () -> ingredientService.findByName(ingredientName));
    }

    @Test
    void saveIngredient() {
        Ingredient ingredient = new Ingredient(1L, "молоко", 500, 1000);

        ingredientService.saveIngredient(ingredient);

        verify(ingredientsRepo, times(1)).save(ingredient);
    }

    @Test
    void testValidateOrderSufficientIngredients(){
        Recipe recipe = new Recipe();
        Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put("молоко", 200);
        recipe.setIngredients(recipeIngredients);

        when(ingredientsRepo.findAll()).thenReturn(List.of(new Ingredient(1L, "молоко", 500, 1500)));

        boolean result = ingredientService.validateOrder(recipe);

        assertTrue(result);
        verify(ingredientsRepo, times(1)).findAll();
    }
    @Test
    void testValidateOrderInsufficientIngredients() {
        Recipe recipe = new Recipe();
        Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put("молоко", 150);
        recipe.setIngredients(recipeIngredients);

        when(ingredientsRepo.findAll()).thenReturn(List.of(new Ingredient(1L, "молоко", 100, 1500)));


        assertThrows(InsufficientIngredientsException.class, () -> ingredientService.validateOrder(recipe));
        verify(ingredientsRepo, times(1)).findAll();
    }
    @Test
    void testValidateOrderMissingIngredient() {
        Recipe recipe = new Recipe();
        Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put("молоко", 150);
        recipe.setIngredients(recipeIngredients);

        when(ingredientsRepo.findAll()).thenReturn(List.of());


        assertThrows(InsufficientIngredientsException.class, () -> ingredientService.validateOrder(recipe));
        verify(ingredientsRepo, times(1)).findAll();
    }

    @Test
    void testReplenishmentsIngredientSuccess() {
        IngredientsRequest request = new IngredientsRequest();
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put("молоко", 100);
        request.setIngredients(ingredients);

        Ingredient existingIngredient = new Ingredient(1L, "молоко", 500, 1500);
        when(ingredientsRepo.findByName("молоко")).thenReturn(Optional.of(existingIngredient));

        List<IngredientReplenishmentResponse> responses = ingredientService.replenishmentsIngredient(request);

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).isSuccess());
        assertEquals(600, responses.get(0).getNewQuantity());
    }
    @Test
    void testReplenishmentsIngredientFailedExceedingTheLimit() {
        IngredientsRequest request = new IngredientsRequest();
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put("молоко", 250);
        request.setIngredients(ingredients);

        Ingredient existingIngredient = new Ingredient(1L, "молоко", 1400, 1500);
        when(ingredientsRepo.findByName("молоко")).thenReturn(Optional.of(existingIngredient));

        List<IngredientReplenishmentResponse> responses = ingredientService.replenishmentsIngredient(request);

        assertEquals(1, responses.size());
        assertFalse(responses.get(0).isSuccess());
        assertNull(responses.get(0).getNewQuantity());
    }
    @Test
    void testReplenishmentsIngredientFailedIngredientNotFound() {
        IngredientsRequest request = new IngredientsRequest();
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put("молоко", 250);
        request.setIngredients(ingredients);

        when(ingredientsRepo.findByName("молоко")).thenReturn(Optional.empty());

        List<IngredientReplenishmentResponse> responses = ingredientService.replenishmentsIngredient(request);

        assertEquals(1, responses.size());
        assertFalse(responses.get(0).isSuccess());
        assertNull(responses.get(0).getNewQuantity());
    }
    @Test
    void testReplenishmentsIngredientFailedNegativeQuantity() {
        IngredientsRequest request = new IngredientsRequest();
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put("молоко", -100);
        request.setIngredients(ingredients);

        List<IngredientReplenishmentResponse> responses = ingredientService.replenishmentsIngredient(request);

        assertEquals(1, responses.size());
        assertFalse(responses.get(0).isSuccess());
        assertNull(responses.get(0).getNewQuantity());
    }
    @Test
    void testReplenishmentsIngredientMultipleIngredients() {
        IngredientsRequest request = new IngredientsRequest();
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put("молоко", 100);
        ingredients.put("молотый кофе", 50);
        request.setIngredients(ingredients);

        Ingredient existingIngredient1 = new Ingredient(1L, "молоко", 1400, 1500);
        Ingredient existingIngredient2 = new Ingredient(2L, "кофе", 500, 1000);
        when(ingredientsRepo.findByName("молоко")).thenReturn(Optional.of(existingIngredient1));
        when(ingredientsRepo.findByName("молотый кофе")).thenReturn(Optional.of(existingIngredient2));

        List<IngredientReplenishmentResponse> responses = ingredientService.replenishmentsIngredient(request);

        assertEquals(2, responses.size());
        assertTrue(responses.get(0).isSuccess());
        assertEquals(1500, responses.get(0).getNewQuantity());
        assertTrue(responses.get(1).isSuccess());
        assertEquals(550, responses.get(1).getNewQuantity());
    }
    @Test
    void testReplenishmentsIngredientMultipleIngredientsMixed() {
        IngredientsRequest request = new IngredientsRequest();
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put("молоко", 100);
        ingredients.put("кофе", 600);
        request.setIngredients(ingredients);

        Ingredient existingIngredient1 = new Ingredient(1L, "молоко", 1400, 1500);
        Ingredient existingIngredient2 = new Ingredient(2L, "кофе", 500, 1000);
        when(ingredientsRepo.findByName("молоко")).thenReturn(Optional.of(existingIngredient1));
        when(ingredientsRepo.findByName("кофе")).thenReturn(Optional.of(existingIngredient2));

        List<IngredientReplenishmentResponse> responses = ingredientService.replenishmentsIngredient(request);

        assertEquals(2, responses.size());
        assertTrue(responses.get(0).isSuccess());
        assertEquals(1500, responses.get(0).getNewQuantity());
        assertFalse(responses.get(1).isSuccess());
        assertNull(responses.get(1).getNewQuantity());
    }
}