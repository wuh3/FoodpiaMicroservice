package com.foodopia.meal.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MealCustomizationDto {

    private String id;

    @NotEmpty(message = "Scheduled meal ID cannot be null or empty")
    private String scheduledMealId;

    @NotEmpty(message = "User ID cannot be null or empty")
    private String userId;

    private LocalDate deliveryDate;

    private String mealTemplateId;

    private List<String> selectedDishIds;

    private double totalCost;

    private double totalPrice;
}