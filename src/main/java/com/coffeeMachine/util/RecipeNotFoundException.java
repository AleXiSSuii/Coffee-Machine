package com.coffeeMachine.util;

import java.util.NoSuchElementException;

public class RecipeNotFoundException extends NoSuchElementException {
    public RecipeNotFoundException(String ex){
        super(ex);
    }
}
