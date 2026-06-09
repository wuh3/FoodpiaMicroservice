package com.foodopia.meal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MealPlanTypeDto {

    private String id;

    @NotBlank(message = "planCode cannot be null or empty")
    private String planCode;

    @NotBlank(message = "displayName cannot be null or empty")
    private String displayName;

    private String description;

    @NotBlank(message = "templateId cannot be null or empty")
    private String templateId;

    @NotEmpty(message = "levels cannot be null or empty")
    @Valid
    private List<PlanLevelDto> levels = new ArrayList<>();

    private boolean isActive = true;
}
