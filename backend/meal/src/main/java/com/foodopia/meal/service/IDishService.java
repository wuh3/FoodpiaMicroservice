package com.foodopia.meal.service;

import com.foodopia.meal.dto.DishDto;

import java.util.List;

public interface IDishService {

    /**
     * Create a new dish
     * @param dishDto - DishDto object
     */
    void createDish(DishDto dishDto);

    /**
     * Fetch dish details by ID
     * @param id - Dish ID
     * @return DishDto
     */
    DishDto fetchDish(String id);

    /**
     * Fetch all dishes
     * @return List of DishDto
     */
    List<DishDto> fetchAllDishes();

    /**
     * Fetch dishes by category
     * @param category - Dish category
     * @return List of DishDto
     */
    List<DishDto> fetchDishesByCategory(String category);

    /**
     * Fetch dishes that include a dietary tag
     * @param dietaryTag - Dietary tag (e.g. vegan, halal, gluten-free)
     * @return List of DishDto
     */
    List<DishDto> fetchDishesByDietaryTag(String dietaryTag);

    /**
     * Fetch dishes with popularity score at or above the minimum
     * @param minPopularityScore - Minimum popularity score (inclusive)
     * @return List of DishDto
     */
    List<DishDto> fetchDishesByMinPopularityScore(double minPopularityScore);

    /**
     * Fetch dishes that include an ingredient
     * @param ingredientId - Ingredient ID
     * @return List of DishDto
     */
    List<DishDto> fetchDishesByIngredientId(String ingredientId);

    /**
     * Fetch dishes that include an ingredient, resolved by unique ingredient name
     * @param ingredientName - Ingredient name
     * @return List of DishDto
     */
    List<DishDto> fetchDishesByIngredientName(String ingredientName);

    /**
     * Update dish details
     * @param dishDto - DishDto object
     * @return boolean indicating success
     */
    boolean updateDish(DishDto dishDto);
}