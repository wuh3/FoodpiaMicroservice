package com.foodopia.meal.service.impl;

import com.foodopia.meal.dto.DishDto;
import com.foodopia.meal.entity.Dish;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.DishMapper;
import com.foodopia.meal.repository.DishRepository;
import com.foodopia.meal.service.IDishService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DishServiceImpl implements IDishService {

    private DishRepository dishRepository;

    @Override
    public void createDish(DishDto dishDto) {
        // Check if dish already exists
        if (dishRepository.existsByName(dishDto.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Dish already exists with name: " + dishDto.getName());
        }

        Dish dish = DishMapper.mapToDish(dishDto, Dish.builder().build());
        dish.setCreatedAt(LocalDateTime.now());
        dish.setUpdatedAt(LocalDateTime.now());
        dishRepository.save(dish);
    }

    @Override
    public DishDto fetchDish(String id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", id));

        return DishMapper.mapToDishDto(dish, new DishDto());
    }

    @Override
    public List<DishDto> fetchAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return dishes.stream()
                .map(dish -> DishMapper.mapToDishDto(dish, new DishDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DishDto> fetchDishesByCategory(String category) {
        List<Dish> dishes = dishRepository.findByCategory(category);
        return dishes.stream()
                .map(dish -> DishMapper.mapToDishDto(dish, new DishDto()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateDish(DishDto dishDto) {
        Dish dish = dishRepository.findById(dishDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", dishDto.getId()));

        DishMapper.mapToDish(dishDto, dish);
        dish.setUpdatedAt(LocalDateTime.now());
        dishRepository.save(dish);
        return true;
    }
}