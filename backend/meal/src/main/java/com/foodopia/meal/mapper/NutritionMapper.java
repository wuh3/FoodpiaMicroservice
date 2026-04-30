package com.foodopia.meal.mapper;

import com.foodopia.meal.dto.NutritionFactsDto;
import com.foodopia.meal.entity.NutritionFacts;

public class NutritionMapper {
    public static NutritionFactsDto mapToDto(NutritionFacts facts, NutritionFactsDto dto) {
        if (facts == null) {
            return null;
        }
        dto.setCaloriesKcal(facts.getCaloriesKcal());
        dto.setProteinG(facts.getProteinG());
        dto.setSugarG(facts.getSugarG());
        dto.setSaltMg(facts.getSaltMg());
        return dto;
    }

    public static NutritionFacts mapToEntity(NutritionFactsDto dto, NutritionFacts facts) {
        if (dto == null) {
            return null;
        }
        facts.setCaloriesKcal(dto.getCaloriesKcal());
        facts.setProteinG(dto.getProteinG());
        facts.setSugarG(dto.getSugarG());
        facts.setSaltMg(dto.getSaltMg());
        return facts;
    }
}

