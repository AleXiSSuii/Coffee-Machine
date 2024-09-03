package com.coffeeMachine.util;

public class InsufficientIngredientsException extends IllegalArgumentException{
    public InsufficientIngredientsException(String ex){
        super(ex);
    }
}
