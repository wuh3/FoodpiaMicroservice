package com.foodopia.meal.service;

import com.foodopia.meal.dto.MealTemplateDto;

import java.util.List;

public interface IMealTemplateService {

    /**
     * Create a new meal template
     * @param mealTemplateDto - MealTemplateDto object
     */
    void createMealTemplate(MealTemplateDto mealTemplateDto);

    /**
     * Fetch meal template by ID
     * @param id - Template ID
     * @return MealTemplateDto
     */
    MealTemplateDto fetchMealTemplate(String id);

    /**
     * Fetch all meal templates
     * @return List of MealTemplateDto
     */
    List<MealTemplateDto> fetchAllMealTemplates();

    /**
     * Validate if dish selection matches template
     * @param templateId - Template ID
     * @param dishIds - List of selected dish IDs
     * @return boolean indicating if selection is valid
     */
    boolean validateDishSelection(String templateId, List<String> dishIds);
}