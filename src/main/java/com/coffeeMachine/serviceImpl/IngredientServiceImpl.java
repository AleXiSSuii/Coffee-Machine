package com.coffeeMachine.serviceImpl;

import com.coffeeMachine.dto.request.IngredientsRequest;
import com.coffeeMachine.dto.response.IngredientReplenishmentResponse;
import com.coffeeMachine.model.Ingredient;
import com.coffeeMachine.model.Recipe;
import com.coffeeMachine.repo.IngredientsRepo;
import com.coffeeMachine.service.IngredientService;
import com.coffeeMachine.util.InsufficientIngredientsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {


    private final IngredientsRepo ingredientsRepo;

    @Autowired
    public IngredientServiceImpl(IngredientsRepo ingredientsRepo) {
        this.ingredientsRepo = ingredientsRepo;
    }

    @Override
    public Ingredient findByName(String ingredientName) {
        Optional<Ingredient> ingredient = ingredientsRepo.findByName(ingredientName);
        if (ingredient.isPresent()) {
            return ingredient.get();
        } else {
            throw new InsufficientIngredientsException("Нет такого ингридиента");
        }
    }

    @Override
    public void saveIngredient(Ingredient ingredient) {
        ingredientsRepo.save(ingredient);
    }

    @Override
    public boolean validateOrder(Recipe recipe) throws InsufficientIngredientsException {
        Map<String, Integer> recipeIngredients = recipe.getIngredients();
        Map<String, Integer> availableIngredientsMap = ingredientsRepo.findAll()
                .stream()
                .collect(Collectors.toMap(Ingredient::getName, Ingredient::getQuantity));
        for (Map.Entry<String, Integer> recipeIngredient : recipeIngredients.entrySet()) {
            String ingredientName = recipeIngredient.getKey();
            int requiredQuantity = recipeIngredient.getValue();
            int availableQuantity = availableIngredientsMap.getOrDefault(ingredientName, 0);
            if (availableQuantity < requiredQuantity) {
                log.info("Недостаточно: {} (требуется: {}, имеется: {})", ingredientName, requiredQuantity, availableQuantity);
                throw new InsufficientIngredientsException("Не достаточно ингридиента: " + ingredientName + ".Нужно: " + requiredQuantity + ". Доступно: " + availableQuantity);
            }
        }
        log.info("Заказ принят в обработку");
        return true;
    }

    @Override
    public List<IngredientReplenishmentResponse> replenishmentsIngredient(IngredientsRequest request) {
        Map<String, Integer> ingredientsStock = request.getIngredients();
        List<IngredientReplenishmentResponse> responses = new ArrayList<>();
        for (Map.Entry<String, Integer> ingredientEntry : ingredientsStock.entrySet()) {
            String ingredientName = ingredientEntry.getKey();
            Integer requestedQuantity = ingredientEntry.getValue();
            if (requestedQuantity <= 0) {
                log.warn("Новое количество ингредиента {} должно быть положительным", ingredientName);
                responses.add(new IngredientReplenishmentResponse(ingredientName, false, null, "Новое количество должно быть положительным у ингридиента: " + ingredientName));
                continue;
            }

            Optional<Ingredient> existingIngredient = ingredientsRepo.findByName(ingredientName);

            if (existingIngredient.isPresent()) {
                Ingredient ingredient = existingIngredient.get();
                int newQuantity = ingredient.getQuantity() + requestedQuantity;
                if (newQuantity <= ingredient.getMaxQuantity()) {
                    ingredient.setQuantity(newQuantity);
                    ingredientsRepo.save(ingredient);
                    responses.add(new IngredientReplenishmentResponse(ingredientName, true, newQuantity, null));
                } else {
                    log.warn("Максимальное допустимое количество ингредиента {}: {}, запрошено: {}", ingredientName, ingredient.getMaxQuantity(), requestedQuantity);
                    responses.add(new IngredientReplenishmentResponse(ingredientName, false, null, "Превышено максимальное количество ингридиента: " + ingredientName));
                }
            } else {
                log.error("Ингредиент {} не найден", ingredientName);
                responses.add(new IngredientReplenishmentResponse(ingredientName, false, null, "Ингредиент не найден"));
            }
        }
        return responses;
    }
}
