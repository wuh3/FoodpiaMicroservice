package com.foodopia.meal.repository;

import com.foodopia.meal.entity.Ingredient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends MongoRepository<Ingredient, String> {

    // Find ingredient by name (case-insensitive)
    Optional<Ingredient> findByNameIgnoreCase(String name);

    // Find ingredients by category
    List<Ingredient> findByCategory(String category);

    // Check if ingredient exists by name (case-insensitive)
    boolean existsByNameIgnoreCase(String name);
}