package com.foodopia.meal.service.impl;

import com.foodopia.meal.dto.MealCustomizationDto;
import com.foodopia.meal.entity.Dish;
import com.foodopia.meal.entity.MealCustomization;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.repository.DishRepository;
import com.foodopia.meal.repository.MealCustomizationRepository;
import com.foodopia.meal.service.IMealCustomizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MealCustomizationServiceImpl implements IMealCustomizationService {

    private MealCustomizationRepository customizationRepository;
    private DishRepository dishRepository;

    @Override
    public void createCustomization(MealCustomizationDto customizationDto) {
        // Check if customization already exists for this scheduled meal
        if (customizationRepository.existsByScheduledMealId(customizationDto.getScheduledMealId())) {
            throw new ResourceAlreadyExistsException(
                    "Customization already exists for scheduled meal: " + customizationDto.getScheduledMealId());
        }

        // Fetch selected dishes
        List<Dish> dishes = customizationDto.getSelectedDishIds().stream()
                .map(id -> dishRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", id)))
                .collect(Collectors.toList());

        MealCustomization customization = MealCustomization.builder()
                .scheduledMealId(customizationDto.getScheduledMealId())
                .userId(customizationDto.getUserId())
                .deliveryDate(customizationDto.getDeliveryDate())
                .mealTemplateId(customizationDto.getMealTemplateId())
                .selectedDishes(dishes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Calculate costs
        customization.setTotalCost(customization.calculateCost());
        customization.setTotalPrice(customization.calculatePrice(0.3)); // 30% markup

        customizationRepository.save(customization);
    }

    @Override
    public MealCustomizationDto fetchCustomizationByScheduledMeal(String scheduledMealId) {
        MealCustomization customization = customizationRepository.findByScheduledMealId(scheduledMealId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MealCustomization", "scheduledMealId", scheduledMealId));

        return mapToDto(customization);
    }

    @Override
    public List<MealCustomizationDto> fetchCustomizationsByUser(String userId) {
        List<MealCustomization> customizations = customizationRepository.findByUserId(userId);
        return customizations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MealCustomizationDto> fetchCustomizationsByDate(LocalDate deliveryDate) {
        List<MealCustomization> customizations = customizationRepository.findByDeliveryDate(deliveryDate);
        return customizations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateCustomization(MealCustomizationDto customizationDto) {
        MealCustomization customization = customizationRepository.findById(customizationDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MealCustomization", "id", customizationDto.getId()));

        // Fetch updated dishes
        List<Dish> dishes = customizationDto.getSelectedDishIds().stream()
                .map(id -> dishRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", id)))
                .collect(Collectors.toList());

        customization.setSelectedDishes(dishes);
        customization.setUpdatedAt(LocalDateTime.now());

        // Recalculate costs
        customization.setTotalCost(customization.calculateCost());
        customization.setTotalPrice(customization.calculatePrice(0.3));

        customizationRepository.save(customization);
        return true;
    }

    // Helper method to map entity to DTO
    private MealCustomizationDto mapToDto(MealCustomization customization) {
        MealCustomizationDto dto = new MealCustomizationDto();
        dto.setId(customization.getId());
        dto.setScheduledMealId(customization.getScheduledMealId());
        dto.setUserId(customization.getUserId());
        dto.setDeliveryDate(customization.getDeliveryDate());
        dto.setMealTemplateId(customization.getMealTemplateId());
        dto.setSelectedDishIds(customization.getSelectedDishes().stream()
                .map(Dish::getId)
                .collect(Collectors.toList()));
        dto.setTotalCost(customization.getTotalCost());
        dto.setTotalPrice(customization.getTotalPrice());
        return dto;
    }
}