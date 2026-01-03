package com.foodopia.meal.service;

import com.foodopia.meal.dto.MealCustomizationDto;

import java.time.LocalDate;
import java.util.List;

public interface IMealCustomizationService {

    /**
     * Create meal customization
     * @param customizationDto - MealCustomizationDto object
     */
    void createCustomization(MealCustomizationDto customizationDto);

    /**
     * Fetch customization by scheduled meal ID
     * @param scheduledMealId - Scheduled meal ID
     * @return MealCustomizationDto
     */
    MealCustomizationDto fetchCustomizationByScheduledMeal(String scheduledMealId);

    /**
     * Fetch customizations by user ID
     * @param userId - User ID
     * @return List of MealCustomizationDto
     */
    List<MealCustomizationDto> fetchCustomizationsByUser(String userId);

    /**
     * Fetch customizations by delivery date
     * @param deliveryDate - Delivery date
     * @return List of MealCustomizationDto
     */
    List<MealCustomizationDto> fetchCustomizationsByDate(LocalDate deliveryDate);

    /**
     * Update meal customization
     * @param customizationDto - MealCustomizationDto object
     * @return boolean indicating success
     */
    boolean updateCustomization(MealCustomizationDto customizationDto);
}