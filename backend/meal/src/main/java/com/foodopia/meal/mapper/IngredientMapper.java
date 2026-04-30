package com.foodopia.meal.mapper;

import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.entity.Ingredient;
import com.foodopia.meal.entity.NutritionFacts;

public class IngredientMapper {

    public static IngredientDto mapToIngredientDto(Ingredient ingredient, IngredientDto ingredientDto) {
        ingredientDto.setId(ingredient.getId());
        ingredientDto.setName(ingredient.getName());
        ingredientDto.setUnitPrice(ingredient.getUnitPrice());
        ingredientDto.setCategory(ingredient.getCategory());
        ingredientDto.setUnit(ingredient.getUnit());
        ingredientDto.setNutritionPer100g(
                NutritionMapper.mapToDto(ingredient.getNutritionPer100g(), new com.foodopia.meal.dto.NutritionFactsDto()));
        return ingredientDto;
    }

    public static Ingredient mapToIngredient(IngredientDto ingredientDto, Ingredient ingredient) {
        ingredient.setName(ingredientDto.getName());
        ingredient.setUnitPrice(ingredientDto.getUnitPrice());
        ingredient.setCategory(ingredientDto.getCategory());
        ingredient.setUnit(ingredientDto.getUnit());
        NutritionFacts facts = ingredient.getNutritionPer100g() != null ? ingredient.getNutritionPer100g() : new NutritionFacts();
        ingredient.setNutritionPer100g(NutritionMapper.mapToEntity(ingredientDto.getNutritionPer100g(), facts));
        return ingredient;
    }
}