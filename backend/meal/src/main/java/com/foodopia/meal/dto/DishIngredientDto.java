package com.foodopia.meal.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DishIngredientDto {

    private IngredientDto ingredient;

    @Positive(message = "Quantity must be greater than zero")
    private double quantity;

    private double cost;
}