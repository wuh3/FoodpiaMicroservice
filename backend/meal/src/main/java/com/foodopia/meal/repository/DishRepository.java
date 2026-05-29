package com.foodopia.meal.repository;

import com.foodopia.meal.entity.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends MongoRepository<Dish, String> {

    // Find dish by name
    Optional<Dish> findByName(String name);

    // Find dishes by category
    List<Dish> findByCategory(String category);

    // Find dishes that include a dietary tag (case-insensitive, e.g. vegan, gluten-free)
    List<Dish> findByDietaryTagsContainingIgnoreCase(String dietaryTag);

    // Find dishes at or above a minimum popularity score
    List<Dish> findByPopularityScoreGreaterThanEqual(double minPopularityScore);

    // Find dishes containing an ingredient (embedded list field)
    List<Dish> findByIngredientsIngredientId(String ingredientId);

    // Check if dish exists by name
    boolean existsByName(String name);
}