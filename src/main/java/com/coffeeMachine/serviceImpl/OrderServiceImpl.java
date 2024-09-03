package com.coffeeMachine.serviceImpl;

import com.coffeeMachine.dto.request.OrderRequest;
import com.coffeeMachine.model.Ingredient;
import com.coffeeMachine.model.Order;
import com.coffeeMachine.model.Recipe;
import com.coffeeMachine.repo.OrderRepo;
import com.coffeeMachine.service.OrderService;
import com.coffeeMachine.util.InsufficientIngredientsException;
import com.coffeeMachine.util.RecipeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService {


    private final RecipeServiceImpl recipeService;
    private final IngredientServiceImpl ingredientService;
    private final OrderRepo orderRepo;

    @Autowired
    public OrderServiceImpl(RecipeServiceImpl recipeService, OrderRepo orderRepo, IngredientServiceImpl ingredientService) {
        this.recipeService = recipeService;
        this.orderRepo = orderRepo;
        this.ingredientService = ingredientService;

    }

    @Transactional
    @Override
    public void orderDrink(OrderRequest request) throws RecipeNotFoundException, InsufficientIngredientsException {
        Recipe recipe = recipeService.getRecipeByName(request.getName());
        if (recipe == null) {
            throw new RecipeNotFoundException("Такого рецепта не существует");
        }
        if (!ingredientService.validateOrder(recipe)) {
            throw new InsufficientIngredientsException("Недостаточно ингридиентов для напитка");
        }
        fulfillOrder(recipe);
        log.info("Заказ успешно обработан: {}", request.getName());
    }

    protected void fulfillOrder(Recipe recipe) {
        Order order = new Order();
        order.setNameRecipe(recipe.getName());
        order.setCreatedAt(LocalDateTime.now());
        orderRepo.save(order);

        for (Map.Entry<String, Integer> recipeIngredient : recipe.getIngredients().entrySet()) {
            String ingredientName = recipeIngredient.getKey();
            int requiredQuantity = recipeIngredient.getValue();
            Ingredient ingredient = ingredientService.findByName(ingredientName);
            ingredient.setQuantity(ingredient.getQuantity() - requiredQuantity);
            ingredientService.saveIngredient(ingredient);
        }
    }


    @Transactional
    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllOrdersOlderFiveYears() {
        LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);
        orderRepo.deleteByCreatedAtBefore(fiveYearsAgo);
        log.info("Старые заказы были удалены");
    }
}
