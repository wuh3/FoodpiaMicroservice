package com.foodopia.meal.service;

import com.foodopia.meal.dto.IngredientDto;

import java.util.List;

public interface IIngredientService {

    /**
     * Create a new ingredient
     * @param ingredientDto - IngredientDto object
     */
    void createIngredient(IngredientDto ingredientDto);

    /**
     * Fetch ingredient details by ID
     * @param id - Ingredient ID
     * @return IngredientDto
     */
    IngredientDto fetchIngredient(String id);

    /**
     * Fetch ingredient details by unique name
     * @param name - Ingredient name
     * @return IngredientDto
     */
    IngredientDto fetchIngredientByName(String name);

    /**
     * Fetch all ingredients
     * @return List of IngredientDto
     */
    List<IngredientDto> fetchAllIngredients();

    /**
     * Fetch ingredients by category
     * @param category - Ingredient category
     * @return List of IngredientDto
     */
    List<IngredientDto> fetchIngredientsByCategory(String category);

    /**
     * Update ingredient price
     * @param id - Ingredient ID
     * @param newPrice - New unit price
     * @return boolean indicating success
     */
    boolean updateIngredientPrice(String id, double newPrice);
}