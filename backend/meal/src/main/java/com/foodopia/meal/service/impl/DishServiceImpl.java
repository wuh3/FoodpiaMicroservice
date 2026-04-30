package com.foodopia.meal.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.foodopia.meal.dto.DishDto;
import com.foodopia.meal.entity.Dish;
import com.foodopia.meal.entity.Ingredient;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.DishMapper;
import com.foodopia.meal.repository.DishRepository;
import com.foodopia.meal.repository.IngredientRepository;
import com.foodopia.meal.service.IDishService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DishServiceImpl implements IDishService {

    private static final Logger log = LoggerFactory.getLogger(DishServiceImpl.class);
    private DishRepository dishRepository;
    private IngredientRepository ingredientRepository;

    @Override
    public void createDish(DishDto dishDto) {
        log.debug("Creating dish with name: {}", dishDto.getName());
        // Check if dish already exists
        if (dishRepository.existsByName(dishDto.getName())) {
            log.warn("Attempted to create dish that already exists: {}", dishDto.getName());
            throw new ResourceAlreadyExistsException(
                    "Dish already exists with name: " + dishDto.getName());
        }

        Dish dish = DishMapper.mapToDish(dishDto, Dish.builder().build());
        recalculateAndSetTotalCost(dish);
        dish.setCreatedAt(LocalDateTime.now());
        dish.setUpdatedAt(LocalDateTime.now());
        dishRepository.save(dish);
        log.debug("Successfully created dish with id: {} and name: {}", dish.getId(), dish.getName());
    }

    @Override
    public DishDto fetchDish(String id) {
        log.debug("Fetching dish with id: {}", id);
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Dish not found with id: {}", id);
                    return new ResourceNotFoundException("Dish", "id", id);
                });

        log.debug("Successfully fetched dish with id: {} and name: {}", id, dish.getName());
        Map<String, Ingredient> ingredientsById = fetchIngredientsForDish(dish);
        DishDto dto = DishMapper.mapToDishDto(dish, new DishDto(), ingredientsById);
        setIngredientLineCosts(dto);
        return dto;
    }

    @Override
    public List<DishDto> fetchAllDishes() {
        log.debug("Fetching all dishes");
        List<Dish> dishes = dishRepository.findAll();
        log.debug("Found {} dishes", dishes.size());
        Map<String, Ingredient> ingredientsById = fetchIngredientsForDishes(dishes);
        List<DishDto> dtos = dishes.stream()
                .map(dish -> DishMapper.mapToDishDto(dish, new DishDto(), ingredientsById))
                .collect(Collectors.toList());
        dtos.forEach(this::setIngredientLineCosts);
        return dtos;
    }

    @Override
    public List<DishDto> fetchDishesByCategory(String category) {
        log.debug("Fetching dishes by category: {}", category);
        List<Dish> dishes = dishRepository.findByCategory(category);
        log.debug("Found {} dishes in category: {}", dishes.size(), category);
        Map<String, Ingredient> ingredientsById = fetchIngredientsForDishes(dishes);
        List<DishDto> dtos = dishes.stream()
                .map(dish -> DishMapper.mapToDishDto(dish, new DishDto(), ingredientsById))
                .collect(Collectors.toList());
        dtos.forEach(this::setIngredientLineCosts);
        return dtos;
    }

    @Override
    public boolean updateDish(DishDto dishDto) {
        log.debug("Updating dish with id: {}", dishDto.getId());
        Dish dish = dishRepository.findById(dishDto.getId())
                .orElseThrow(() -> {
                    log.error("Dish not found for update with id: {}", dishDto.getId());
                    return new ResourceNotFoundException("Dish", "id", dishDto.getId());
                });

        DishMapper.mapToDish(dishDto, dish);
        recalculateAndSetTotalCost(dish);
        dish.setUpdatedAt(LocalDateTime.now());
        dishRepository.save(dish);
        log.debug("Successfully updated dish with id: {}", dishDto.getId());
        return true;
    }

    private void recalculateAndSetTotalCost(Dish dish) {
        if (dish.getIngredients() == null || dish.getIngredients().isEmpty()) {
            dish.setTotalCost(0.0);
            return;
        }

        Set<String> ingredientIds = dish.getIngredients().stream()
                .map(di -> di.getIngredientId())
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());

        Map<String, Ingredient> ingredientsById = ingredientRepository.findAllById(ingredientIds).stream()
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        for (String ingredientId : ingredientIds) {
            if (!ingredientsById.containsKey(ingredientId)) {
                throw new ResourceNotFoundException("Ingredient", "id", ingredientId);
            }
        }

        double total = dish.getIngredients().stream()
                .mapToDouble(di -> {
                    Ingredient ingredient = ingredientsById.get(di.getIngredientId());
                    return ingredient.getUnitPrice() * di.getQuantity();
                })
                .sum();

        dish.setTotalCost(total);
    }

    private Map<String, Ingredient> fetchIngredientsForDish(Dish dish) {
        if (dish.getIngredients() == null || dish.getIngredients().isEmpty()) {
            return Map.of();
        }
        Set<String> ids = dish.getIngredients().stream()
                .map(di -> di.getIngredientId())
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());
        return ingredientRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));
    }

    private Map<String, Ingredient> fetchIngredientsForDishes(List<Dish> dishes) {
        if (dishes == null || dishes.isEmpty()) {
            return Map.of();
        }
        Set<String> ids = new HashSet<>();
        for (Dish dish : dishes) {
            if (dish.getIngredients() == null) continue;
            for (var di : dish.getIngredients()) {
                if (di.getIngredientId() != null && !di.getIngredientId().isBlank()) {
                    ids.add(di.getIngredientId());
                }
            }
        }
        if (ids.isEmpty()) {
            return Map.of();
        }
        return ingredientRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));
    }

    private void setIngredientLineCosts(DishDto dishDto) {
        if (dishDto.getIngredients() == null) return;
        for (var di : dishDto.getIngredients()) {
            if (di.getIngredient() == null) {
                di.setCost(0.0);
                continue;
            }
            di.setCost(di.getIngredient().getUnitPrice() * di.getQuantity());
        }
    }
}