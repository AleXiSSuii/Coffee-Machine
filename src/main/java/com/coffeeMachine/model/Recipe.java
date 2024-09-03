package com.coffeeMachine.model;

import com.coffeeMachine.converter.IngredientMapConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Convert(converter =  IngredientMapConverter.class)
    private Map<String, Integer> ingredients;

    @Column(nullable = false)
    @Min(1)
    private Integer preparationTime;


}
