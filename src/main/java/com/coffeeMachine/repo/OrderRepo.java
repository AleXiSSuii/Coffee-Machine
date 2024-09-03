package com.coffeeMachine.repo;

import com.coffeeMachine.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {
    @Query("SELECT o.nameRecipe FROM Order o GROUP BY o.nameRecipe ORDER BY COUNT(o) DESC LIMIT 1")
    String findMostPopularRecipe();

    void deleteByCreatedAtBefore(LocalDateTime createdAt);
}
