package com.foodopia.meal.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.entity.Dish;
import com.foodopia.meal.entity.Ingredient;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.IngredientMapper;
import com.foodopia.meal.repository.DishRepository;
import com.foodopia.meal.repository.IngredientRepository;
import com.foodopia.meal.service.IIngredientService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IngredientServiceImpl implements IIngredientService {

    private static final Logger log = LoggerFactory.getLogger(IngredientServiceImpl.class);
    private IngredientRepository ingredientRepository;
    private DishRepository dishRepository;

    @Override
    public void createIngredient(IngredientDto ingredientDto) {
        log.debug("Creating ingredient with name: {}", ingredientDto.getName());
        // Check if ingredient already exists
        if (ingredientRepository.existsByName(ingredientDto.getName())) {
            log.warn("Attempted to create ingredient that already exists: {}", ingredientDto.getName());
            throw new ResourceAlreadyExistsException(
                    "Ingredient already exists with name: " + ingredientDto.getName());
        }

        Ingredient ingredient = IngredientMapper.mapToIngredient(ingredientDto, new Ingredient(
                null, null, 0, null, "g"));
        ingredientRepository.save(ingredient);
        log.debug("Successfully created ingredient with id: {} and name: {}", ingredient.getId(), ingredient.getName());
    }

    @Override
    public IngredientDto fetchIngredient(String id) {
        log.debug("Fetching ingredient with id: {}", id);
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ingredient not found with id: {}", id);
                    return new ResourceNotFoundException("Ingredient", "id", id);
                });

        log.debug("Successfully fetched ingredient with id: {} and name: {}", id, ingredient.getName());
        return IngredientMapper.mapToIngredientDto(ingredient, new IngredientDto());
    }

    @Override
    public List<IngredientDto> fetchAllIngredients() {
        log.debug("Fetching all ingredients");
        List<Ingredient> ingredients = ingredientRepository.findAll();
        log.debug("Found {} ingredients", ingredients.size());
        return ingredients.stream()
                .map(ingredient -> IngredientMapper.mapToIngredientDto(ingredient, new IngredientDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<IngredientDto> fetchIngredientsByCategory(String category) {
        log.debug("Fetching ingredients by category: {}", category);
        List<Ingredient> ingredients = ingredientRepository.findByCategory(category);
        log.debug("Found {} ingredients in category: {}", ingredients.size(), category);
        return ingredients.stream()
                .map(ingredient -> IngredientMapper.mapToIngredientDto(ingredient, new IngredientDto()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateIngredientPrice(String id, double newPrice) {
        log.debug("Updating ingredient price for id: {} to new price: {}", id, newPrice);
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ingredient not found for price update with id: {}", id);
                    return new ResourceNotFoundException("Ingredient", "id", id);
                });

        double oldPrice = ingredient.getUnitPrice();
        ingredient.setUnitPrice(newPrice);
        ingredientRepository.save(ingredient);

        // Recalculate costs for dishes that reference this ingredient
        try {
            List<Dish> affectedDishes = dishRepository.findByIngredientsIngredientId(id);
            if (!affectedDishes.isEmpty()) {
                Set<String> ingredientIds = new HashSet<>();
                for (Dish dish : affectedDishes) {
                    if (dish.getIngredients() == null) continue;
                    dish.getIngredients().forEach(di -> {
                        if (di.getIngredientId() != null && !di.getIngredientId().isBlank()) {
                            ingredientIds.add(di.getIngredientId());
                        }
                    });
                }
                Map<String, Ingredient> ingredientsById = ingredientRepository.findAllById(ingredientIds).stream()
                        .collect(Collectors.toMap(Ingredient::getId, Function.identity()));

                for (Dish dish : affectedDishes) {
                    double total = 0.0;
                    if (dish.getIngredients() != null) {
                        for (var di : dish.getIngredients()) {
                            Ingredient ing = ingredientsById.get(di.getIngredientId());
                            if (ing == null) continue;
                            total += ing.getUnitPrice() * di.getQuantity();
                        }
                    }
                    dish.setTotalCost(total);
                }
                dishRepository.saveAll(affectedDishes);
                log.debug("Recalculated totalCost for {} dishes affected by ingredient {}", affectedDishes.size(), id);
            }
        } catch (Exception e) {
            log.warn("Failed to recalculate dish costs after ingredient price update for id: {}", id, e);
        }

        log.debug("Successfully updated ingredient price for id: {} from {} to {}", id, oldPrice, newPrice);
        return true;
    }
}