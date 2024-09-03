package com.coffeeMachine.repo;

import com.coffeeMachine.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe,Long>{
    Optional<Recipe> findByName(String name);
}
