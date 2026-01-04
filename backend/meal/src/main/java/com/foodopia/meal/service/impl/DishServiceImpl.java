package com.foodopia.meal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.foodopia.meal.dto.DishDto;
import com.foodopia.meal.entity.Dish;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.DishMapper;
import com.foodopia.meal.repository.DishRepository;
import com.foodopia.meal.service.IDishService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DishServiceImpl implements IDishService {

    private static final Logger log = LoggerFactory.getLogger(DishServiceImpl.class);
    private DishRepository dishRepository;

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
        return DishMapper.mapToDishDto(dish, new DishDto());
    }

    @Override
    public List<DishDto> fetchAllDishes() {
        log.debug("Fetching all dishes");
        List<Dish> dishes = dishRepository.findAll();
        log.debug("Found {} dishes", dishes.size());
        return dishes.stream()
                .map(dish -> DishMapper.mapToDishDto(dish, new DishDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DishDto> fetchDishesByCategory(String category) {
        log.debug("Fetching dishes by category: {}", category);
        List<Dish> dishes = dishRepository.findByCategory(category);
        log.debug("Found {} dishes in category: {}", dishes.size(), category);
        return dishes.stream()
                .map(dish -> DishMapper.mapToDishDto(dish, new DishDto()))
                .collect(Collectors.toList());
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
        dish.setUpdatedAt(LocalDateTime.now());
        dishRepository.save(dish);
        log.debug("Successfully updated dish with id: {}", dishDto.getId());
        return true;
    }
}