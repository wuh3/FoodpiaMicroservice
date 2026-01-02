package com.foodopia.meal.mapper;

import com.foodopia.meal.dto.MealTemplateDto;
import com.foodopia.meal.entity.MealTemplate;

public class MealTemplateMapper {

    public static MealTemplateDto mapToMealTemplateDto(MealTemplate mealTemplate, MealTemplateDto mealTemplateDto) {
        mealTemplateDto.setId(mealTemplate.getId());
        mealTemplateDto.setName(mealTemplate.getName());
        mealTemplateDto.setDishCategories(mealTemplate.getDishCategories());
        mealTemplateDto.setTotalDishes(mealTemplate.getTotalDishes());
        return mealTemplateDto;
    }

    public static MealTemplate mapToMealTemplate(MealTemplateDto mealTemplateDto, MealTemplate mealTemplate) {
        mealTemplate.setName(mealTemplateDto.getName());
        mealTemplate.setDishCategories(mealTemplateDto.getDishCategories());
        mealTemplate.setTotalDishes(mealTemplateDto.getTotalDishes());
        return mealTemplate;
    }
}