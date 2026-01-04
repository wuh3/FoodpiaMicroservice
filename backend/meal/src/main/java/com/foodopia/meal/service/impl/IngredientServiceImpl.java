package com.foodopia.meal.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.entity.Ingredient;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.IngredientMapper;
import com.foodopia.meal.repository.IngredientRepository;
import com.foodopia.meal.service.IIngredientService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IngredientServiceImpl implements IIngredientService {

    private static final Logger log = LoggerFactory.getLogger(IngredientServiceImpl.class);
    private IngredientRepository ingredientRepository;

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
        log.debug("Successfully updated ingredient price for id: {} from {} to {}", id, oldPrice, newPrice);
        return true;
    }
}