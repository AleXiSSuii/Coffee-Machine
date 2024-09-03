package com.coffeeMachine.constant;

import com.coffeeMachine.model.Recipe;

import java.util.Map;

public class RecipeConstants {
    public static final Recipe ESPRESSO = Recipe.builder()
            .name("Эспрессо")
            .ingredients(Map.of("вода", 180, "молотый кофе", 10))
            .preparationTime(25)
            .build();
    public static final Recipe AMERICANO = Recipe.builder()
            .name("Американо")
            .ingredients(Map.of("вода", 90, "молотый кофе", 15))
            .preparationTime(30)
            .build();
    public static final Recipe CAPPUCCINO = Recipe.builder()
            .name("Капучино")
            .ingredients(Map.of("вода", 60, "молотый кофе", 7, "молоко", 150))
            .preparationTime(45)
            .build();
    public static Recipe getRecipeByName(String name) {
        switch (name.toUpperCase()) {
            case "ЭСПРЕССО":
                return ESPRESSO;
            case "АМЕРИКАНО":
                return AMERICANO;
            case "КАПУЧИНО":
                return CAPPUCCINO;
            default:
                return null;
        }
    }
}
