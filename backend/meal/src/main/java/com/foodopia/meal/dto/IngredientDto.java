package com.foodopia.meal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class IngredientDto {

    private String id;

    @NotEmpty(message = "Ingredient name cannot be null or empty")
    private String name;

    @Positive(message = "Unit price must be greater than zero")
    private double unitPrice;

    @NotEmpty(message = "Category cannot be null or empty")
    private String category;

    private String unit;
}