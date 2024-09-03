package com.coffeeMachine.controllers;

import com.coffeeMachine.dto.response.IngredientReplenishmentResponse;
import com.coffeeMachine.dto.request.IngredientsRequest;
import com.coffeeMachine.dto.request.OrderRequest;
import com.coffeeMachine.dto.request.RecipeRequest;
import com.coffeeMachine.dto.response.RecipeResponse;
import com.coffeeMachine.model.Recipe;
import com.coffeeMachine.serviceImpl.IngredientServiceImpl;
import com.coffeeMachine.serviceImpl.OrderServiceImpl;
import com.coffeeMachine.serviceImpl.RecipeServiceImpl;
import com.coffeeMachine.util.InsufficientIngredientsException;
import com.coffeeMachine.util.RecipeNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CoffeeMachineController {

    private final RecipeServiceImpl recipeService;
    private final OrderServiceImpl orderService;
    private final IngredientServiceImpl ingredientService;
    private final ObjectMapper objectMapper;
    @Autowired
    public CoffeeMachineController(RecipeServiceImpl recipeService, OrderServiceImpl orderService, IngredientServiceImpl ingredientService, ObjectMapper objectMapper) {
        this.recipeService = recipeService;
        this.orderService = orderService;
        this.ingredientService = ingredientService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/drinks")
    public List<Recipe> getAvailableDrinks(){
        return recipeService.getAllRecipes();
    }

    @GetMapping("/popularDrink")
    public ResponseEntity<String> getMostPopularDrinks(){
        RecipeResponse response = recipeService.getMostPopularRecipe();
        return ResponseEntity.ok(response.getMessage());
    }

    @PostMapping("/order")
    public ResponseEntity<String> orderDrink(@RequestBody OrderRequest orderRequest){
        try {
            orderService.orderDrink(orderRequest);
            return ResponseEntity.ok("Заказ принят!");
        } catch (RecipeNotFoundException | InsufficientIngredientsException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/recipe")
    public ResponseEntity<String> addRecipe(@RequestBody RecipeRequest request){
        RecipeResponse response = recipeService.addNewRecipe(request);
        if(response.isSuccess()){
            return ResponseEntity.ok(response.getMessage());
        }else {
            return ResponseEntity.badRequest().body(response.getMessage());
        }
    }

    @PatchMapping("/ingredients")
    public ResponseEntity<String> replenishmentIngredient(@RequestBody IngredientsRequest request){
        List<IngredientReplenishmentResponse> responses = ingredientService.replenishmentsIngredient(request);
        List<String> userMessages = responses.stream()
                .map(IngredientReplenishmentResponse::getUserMessage)
                .collect(Collectors.toList());
        try {
            String jsonResponse = objectMapper.writeValueAsString(userMessages);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
