package com.coffeeMachine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IngredientReplenishmentResponse {
    private String ingredientName;
    private boolean success;
    private Integer newQuantity;
    private String errorMessage;

    public String getUserMessage() {
        if (success) {
            return String.format("Ингредиент %s успешно пополнен. Новое количество: %d", ingredientName, newQuantity);
        } else {
            return errorMessage != null ? errorMessage : "Произошла ошибка при пополнении ингредиента";
        }
    }
}
