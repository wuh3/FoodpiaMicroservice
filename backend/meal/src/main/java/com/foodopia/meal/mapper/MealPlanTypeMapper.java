package com.foodopia.meal.mapper;

import com.foodopia.meal.dto.MealPlanTypeDto;
import com.foodopia.meal.dto.PlanLevelDto;
import com.foodopia.meal.entity.MealPlanType;
import com.foodopia.meal.entity.PlanLevel;

import java.util.List;
import java.util.stream.Collectors;

public final class MealPlanTypeMapper {

    private MealPlanTypeMapper() {}

    public static MealPlanTypeDto mapToDto(MealPlanType mealPlanType, MealPlanTypeDto dto) {
        dto.setId(mealPlanType.getId());
        dto.setPlanCode(mealPlanType.getPlanCode());
        dto.setDisplayName(mealPlanType.getDisplayName());
        dto.setDescription(mealPlanType.getDescription());
        dto.setTemplateId(mealPlanType.getTemplateId());
        dto.setLevels(mapLevelsToDto(mealPlanType.getLevels()));
        dto.setActive(mealPlanType.isActive());
        return dto;
    }

    public static MealPlanType mapToEntity(MealPlanTypeDto dto, MealPlanType mealPlanType) {
        mealPlanType.setPlanCode(dto.getPlanCode());
        mealPlanType.setDisplayName(dto.getDisplayName());
        mealPlanType.setDescription(dto.getDescription());
        mealPlanType.setTemplateId(dto.getTemplateId());
        mealPlanType.setLevels(mapLevelsToEntity(dto.getLevels()));
        mealPlanType.setActive(dto.isActive());
        return mealPlanType;
    }

    private static List<PlanLevelDto> mapLevelsToDto(List<PlanLevel> levels) {
        if (levels == null) {
            return List.of();
        }
        return levels.stream()
                .map(level -> {
                    PlanLevelDto dto = new PlanLevelDto();
                    dto.setLevel(level.getLevel());
                    dto.setMealsPerMonth(level.getMealsPerMonth());
                    dto.setMonthlyPrice(level.getMonthlyPrice());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static List<PlanLevel> mapLevelsToEntity(List<PlanLevelDto> levels) {
        if (levels == null) {
            return List.of();
        }
        return levels.stream()
                .map(dto -> new PlanLevel(dto.getLevel(), dto.getMealsPerMonth(), dto.getMonthlyPrice()))
                .collect(Collectors.toList());
    }
}
