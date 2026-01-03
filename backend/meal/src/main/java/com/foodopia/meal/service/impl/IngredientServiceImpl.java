package com.foodopia.meal.service.impl;

import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.entity.Ingredient;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.IngredientMapper;
import com.foodopia.meal.repository.IngredientRepository;
import com.foodopia.meal.service.IIngredientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IngredientServiceImpl implements IIngredientService {

    private IngredientRepository ingredientRepository;

    @Override
    public void createIngredient(IngredientDto ingredientDto) {
        // Check if ingredient already exists
        if (ingredientRepository.existsByName(ingredientDto.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Ingredient already exists with name: " + ingredientDto.getName());
        }

        Ingredient ingredient = IngredientMapper.mapToIngredient(ingredientDto, new Ingredient(
                null, null, 0, null, "g"));
        ingredientRepository.save(ingredient);
    }

    @Override
    public IngredientDto fetchIngredient(String id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));

        return IngredientMapper.mapToIngredientDto(ingredient, new IngredientDto());
    }

    @Override
    public List<IngredientDto> fetchAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream()
                .map(ingredient -> IngredientMapper.mapToIngredientDto(ingredient, new IngredientDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<IngredientDto> fetchIngredientsByCategory(String category) {
        List<Ingredient> ingredients = ingredientRepository.findByCategory(category);
        return ingredients.stream()
                .map(ingredient -> IngredientMapper.mapToIngredientDto(ingredient, new IngredientDto()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateIngredientPrice(String id, double newPrice) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));

        ingredient.setUnitPrice(newPrice);
        ingredientRepository.save(ingredient);
        return true;
    }
}