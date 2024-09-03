package com.coffeeMachine.util;

import java.util.NoSuchElementException;

public class IngredientNotFoundException extends NoSuchElementException {

    public IngredientNotFoundException(String ex) {
        super(ex);
    }
}
