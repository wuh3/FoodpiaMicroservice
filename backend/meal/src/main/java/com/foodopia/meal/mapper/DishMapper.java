package com.foodopia.meal.mapper;

import com.foodopia.meal.dto.DishDto;
import com.foodopia.meal.dto.DishIngredientDto;
import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.entity.Dish;
import com.foodopia.meal.entity.DishIngredient;
import com.foodopia.meal.entity.Ingredient;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class DishMapper {

    public static DishDto mapToDishDto(Dish dish, DishDto dishDto, Map<String, Ingredient> ingredientsById) {
        dishDto.setId(dish.getId());
        dishDto.setName(dish.getName());
        dishDto.setDescription(dish.getDescription());
        dishDto.setCategory(dish.getCategory());
        dishDto.setServingSize(dish.getServingSize());
        dishDto.setAvailable(dish.isAvailable());
        dishDto.setAvailableFrom(dish.getAvailableFrom());
        dishDto.setAvailableUntil(dish.getAvailableUntil());
        dishDto.setDietaryTags(dish.getDietaryTags());
        dishDto.setAllergens(dish.getAllergens());
        dishDto.setImageUrl(dish.getImageUrl());
        dishDto.setPopularityScore(dish.getPopularityScore());
        dishDto.setTimesOrdered(dish.getTimesOrdered());
        dishDto.setTotalCost(dish.getTotalCost());

        // Map ingredients
        if (dish.getIngredients() != null) {
            dishDto.setIngredients(dish.getIngredients().stream()
                    .map(di -> mapToDishIngredientDto(di, ingredientsById))
                    .collect(Collectors.toList()));
        }

        return dishDto;
    }

    public static Dish mapToDish(DishDto dishDto, Dish dish) {
        dish.setName(dishDto.getName());
        dish.setDescription(dishDto.getDescription());
        dish.setCategory(dishDto.getCategory());
        dish.setServingSize(dishDto.getServingSize());
        dish.setAvailable(dishDto.isAvailable());
        dish.setAvailableFrom(dishDto.getAvailableFrom());
        dish.setAvailableUntil(dishDto.getAvailableUntil());
        dish.setDietaryTags(dishDto.getDietaryTags());
        dish.setAllergens(dishDto.getAllergens());
        dish.setImageUrl(dishDto.getImageUrl());
        dish.setPopularityScore(dishDto.getPopularityScore());
        dish.setTimesOrdered(dishDto.getTimesOrdered());

        // Map ingredients
        if (dishDto.getIngredients() != null) {
            dish.setIngredients(dishDto.getIngredients().stream()
                    .map(DishMapper::mapToDishIngredient)
                    .collect(Collectors.toList()));
        } else {
            dish.setIngredients(new ArrayList<>());
        }

        return dish;
    }

    private static DishIngredientDto mapToDishIngredientDto(DishIngredient dishIngredient,
                                                           Map<String, Ingredient> ingredientsById) {
        DishIngredientDto dto = new DishIngredientDto();
        dto.setIngredientId(dishIngredient.getIngredientId());
        Ingredient ingredient = ingredientsById.get(dishIngredient.getIngredientId());
        if (ingredient != null) {
            dto.setIngredient(IngredientMapper.mapToIngredientDto(ingredient, new IngredientDto()));
        }
        dto.setQuantity(dishIngredient.getQuantity());
        return dto;
    }

    private static DishIngredient mapToDishIngredient(DishIngredientDto dto) {
        return new DishIngredient(
                dto.getIngredientId(),
                dto.getQuantity()
        );
    }
}