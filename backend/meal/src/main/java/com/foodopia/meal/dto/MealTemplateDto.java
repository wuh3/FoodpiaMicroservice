package com.foodopia.meal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MealTemplateDto {

    private String id;

    @NotEmpty(message = "Template name cannot be null or empty")
    private String name;

    private Map<String, Integer> dishCategories;

    @Positive(message = "Total dishes must be greater than zero")
    private int totalDishes;

    private List<String> requiredTags = new ArrayList<>();

    private List<String> forbiddenTags = new ArrayList<>();
}