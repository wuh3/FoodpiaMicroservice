package com.foodopia.meal.service;

import com.foodopia.meal.dto.DishDto;

import java.time.LocalDate;
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
     * Update dish details
     * @param dishDto - DishDto object
     * @return boolean indicating success
     */
    boolean updateDish(DishDto dishDto);
}