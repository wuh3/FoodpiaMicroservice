package com.foodopia.meal.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PlanLevelDto {

    @Positive(message = "Level must be greater than zero")
    private int level;

    @Positive(message = "Meals per month must be greater than zero")
    private int mealsPerMonth;

    @Positive(message = "Monthly price must be greater than zero")
    private double monthlyPrice;
}
