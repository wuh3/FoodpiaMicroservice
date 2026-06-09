package com.foodopia.meal.service;

import com.foodopia.meal.dto.MealPlanTypeDto;

import java.util.List;

public interface IMealPlanTypeService {

    void createMealPlanType(MealPlanTypeDto mealPlanTypeDto);

    MealPlanTypeDto fetchMealPlanType(String planCode);

    List<MealPlanTypeDto> fetchAllActiveMealPlanTypes();

    boolean updateMealPlanType(String planCode, MealPlanTypeDto mealPlanTypeDto);
}
