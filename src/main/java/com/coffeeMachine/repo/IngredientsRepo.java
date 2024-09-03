package com.coffeeMachine.repo;

import com.coffeeMachine.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientsRepo extends JpaRepository<Ingredient,Long> {
    Optional<Ingredient> findByName(String name);
}
