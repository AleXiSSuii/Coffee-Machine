package com.coffeeMachine.serviceImpl;

import com.coffeeMachine.constant.RecipeConstants;
import com.coffeeMachine.dto.request.RecipeRequest;
import com.coffeeMachine.dto.response.RecipeResponse;
import com.coffeeMachine.model.Recipe;
import com.coffeeMachine.repo.OrderRepo;
import com.coffeeMachine.repo.RecipeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    private RecipeRepo recipeRepo;

    @Mock
    private OrderRepo orderRepo;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    Recipe latte;
    Recipe melange;
    Recipe romano;

    @BeforeEach
    void setUp() {
        latte =  Recipe.builder()
                .id(1)
                .name("Латте")
                .ingredients(new HashMap<>())
                .build();
        melange = Recipe.builder()
                .id(3)
                .name("Меланж")
                .build();
        romano = Recipe.builder()
                .id(3)
                .name("Романо")
                .build();
        latte.getIngredients().put("молотый кофе", 50);
        latte.getIngredients().put("молоко", 150);
    }

    @Test
    void testGetDefaultRecipeByName() {
        String defaultRecipeName1 = "Капучино";
        Recipe expectedRecipe1 = RecipeConstants.CAPPUCCINO;
        Recipe testDefaultRecipe1 = recipeService.getRecipeByName(defaultRecipeName1);

        String defaultRecipeName2 = "Американо";
        Recipe expectedRecipe2 = RecipeConstants.AMERICANO;
        Recipe testDefaultRecipe2 = recipeService.getRecipeByName(defaultRecipeName2);

        String defaultRecipeName3 = "Эспрессо";
        Recipe expectedRecipe3 = RecipeConstants.ESPRESSO;
        Recipe testDefaultRecipe3 = recipeService.getRecipeByName(defaultRecipeName3);

        assertEquals(expectedRecipe1, testDefaultRecipe1);
        assertEquals(expectedRecipe2, testDefaultRecipe2);
        assertEquals(expectedRecipe3, testDefaultRecipe3);
    }

    @Test
    void testGetAllRecipes() {
       List<Recipe> databaseRecipes = List.of(latte,melange,romano);

        when(recipeRepo.findAll()).thenReturn(databaseRecipes);
        List<Recipe> allRecipes = recipeService.getAllRecipes();

        assertEquals(6,allRecipes.size());
        assertTrue(allRecipes.contains(RecipeConstants.CAPPUCCINO));
        assertTrue(allRecipes.contains(RecipeConstants.AMERICANO));
        assertTrue(allRecipes.contains(RecipeConstants.ESPRESSO));
        assertTrue(allRecipes.contains(latte));
        assertTrue(allRecipes.contains(melange));
        assertTrue(allRecipes.contains(romano));
    }

    @Test
    void testAddNewRecipe() {
        RecipeRequest request = new RecipeRequest();
        request.setName("Латте");
        request.setIngredients(new HashMap<>());
        request.setPreparationTime(100);
        request.getIngredients().put("молотый кофе", 50);
        request.getIngredients().put("молоко", 150);

        Mockito.when(recipeRepo.save(Mockito.any(Recipe.class))).thenReturn(latte);

        RecipeResponse response = recipeService.addNewRecipe(request);

        assertTrue(response.isSuccess());
        assertEquals(request.getName(), response.getNameRecipe());
        verify(recipeRepo).save(Mockito.any(Recipe.class));
    }

    @Test
    void testGetMostPopularRecipe() {
        when(orderRepo.findMostPopularRecipe()).thenReturn("Капучино");

        RecipeResponse response = recipeService.getMostPopularRecipe();

        assertTrue(response.isSuccess());
        assertEquals("Капучино", response.getNameRecipe());
        verify(orderRepo, times(1)).findMostPopularRecipe();
    }
}