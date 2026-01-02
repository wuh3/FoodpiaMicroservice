package com.foodopia.meal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DishDto {

    private String id;

    @NotEmpty(message = "Dish name cannot be null or empty")
    private String name;

    private String description;

    private List<DishIngredientDto> ingredients;

    @NotEmpty(message = "Category cannot be null or empty")
    private String category;

    @Positive(message = "Serving size must be greater than zero")
    private int servingSize;

    private boolean isAvailable;

    private LocalDate availableFrom;

    private LocalDate availableUntil;

    private List<String> dietaryTags;

    private List<String> allergens;

    private String imageUrl;

    private double popularityScore;

    private int timesOrdered;

    private double totalCost;
}