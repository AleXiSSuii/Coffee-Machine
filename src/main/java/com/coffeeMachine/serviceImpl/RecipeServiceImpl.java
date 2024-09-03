package com.coffeeMachine.serviceImpl;

import com.coffeeMachine.constant.RecipeConstants;
import com.coffeeMachine.dto.request.RecipeRequest;
import com.coffeeMachine.dto.response.RecipeResponse;
import com.coffeeMachine.model.Recipe;
import com.coffeeMachine.repo.OrderRepo;
import com.coffeeMachine.repo.RecipeRepo;
import com.coffeeMachine.service.RecipeService;
import com.coffeeMachine.util.RecipeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coffeeMachine.constant.RecipeConstants.*;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final Map<String, Recipe> recipeCache = new HashMap<>();

    private final RecipeRepo recipeRepo;

    private final OrderRepo orderRepo;


    @Autowired
    public RecipeServiceImpl(RecipeRepo recipeRepo, OrderRepo orderRepo) {
        this.recipeRepo = recipeRepo;
        this.orderRepo = orderRepo;
    }

    @Override
    public Recipe getRecipeByName(String name) throws RecipeNotFoundException {

        Recipe recipe = recipeCache.get(name);
        if (recipe != null) {
            return recipe;
        }

        recipe = RecipeConstants.getRecipeByName(name);
        if (recipe != null) {
            recipeCache.put(name, recipe);
            return recipe;
        }

        recipe = recipeRepo.findByName(name).orElse(null);
        if (recipe != null) {
            recipeCache.put(name, recipe);
        }
        return recipe;
    }


    @Override
    public List<Recipe> getAllRecipes() {
        List<Recipe> allRecipes = Stream.concat(
                        Stream.of(ESPRESSO, AMERICANO, CAPPUCCINO),
                        recipeRepo.findAll().stream()
                ).distinct()
                .collect(Collectors.toList());
        if (!allRecipes.isEmpty()) {
            return allRecipes;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public RecipeResponse addNewRecipe(RecipeRequest request) {
        if (request != null) {
            Recipe recipe = new Recipe();
            if (getRecipeByName(request.getName()) == null) {
                recipe.setName(request.getName());
                recipe.setIngredients(request.getIngredients());
                recipe.setPreparationTime(request.getPreparationTime());
                recipeRepo.save(recipe);
            } else {
                log.info("Рецепт {} уже существет",recipe.getName());
                return new RecipeResponse(false,recipe.getName(),"Такой рецепт уже существет");
            }
            return new RecipeResponse(true,recipe.getName(),recipe.getName() + " рецепт успешно создан!");
        }
        return new RecipeResponse(false,null,"Произошла ошибка при создании рецепта");
    }

    @Override
    public RecipeResponse getMostPopularRecipe() {
        String mostPopularRecipe = orderRepo.findMostPopularRecipe();
        if (mostPopularRecipe != null) {
            log.info("Самый популярный напиток: {}",mostPopularRecipe);
            getRecipeByName(mostPopularRecipe);
            return new RecipeResponse(true,mostPopularRecipe,"Самый популярный напиток: " + mostPopularRecipe);
        } else {
            log.info("Нет данных о самом популярном напитке");
            return new RecipeResponse(false, null,"Нет данных о самом популярном напитке");
        }

    }
}
