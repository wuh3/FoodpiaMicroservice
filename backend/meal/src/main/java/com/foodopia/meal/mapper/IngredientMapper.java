package com.foodopia.meal.mapper;

import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.entity.Ingredient;

public class IngredientMapper {

    public static IngredientDto mapToIngredientDto(Ingredient ingredient, IngredientDto ingredientDto) {
        ingredientDto.setId(ingredient.getId());
        ingredientDto.setName(ingredient.getName());
        ingredientDto.setUnitPrice(ingredient.getUnitPrice());
        ingredientDto.setCategory(ingredient.getCategory());
        ingredientDto.setUnit(ingredient.getUnit());
        return ingredientDto;
    }

    public static Ingredient mapToIngredient(IngredientDto ingredientDto, Ingredient ingredient) {
        ingredient.setName(ingredientDto.getName());
        ingredient.setUnitPrice(ingredientDto.getUnitPrice());
        ingredient.setCategory(ingredientDto.getCategory());
        ingredient.setUnit(ingredientDto.getUnit());
        return ingredient;
    }
}