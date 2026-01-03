package com.foodopia.meal.service.impl;

import com.foodopia.meal.dto.MealTemplateDto;
import com.foodopia.meal.entity.Dish;
import com.foodopia.meal.entity.MealTemplate;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.MealTemplateMapper;
import com.foodopia.meal.repository.DishRepository;
import com.foodopia.meal.repository.MealTemplateRepository;
import com.foodopia.meal.service.IMealTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MealTemplateServiceImpl implements IMealTemplateService {

    private MealTemplateRepository mealTemplateRepository;
    private DishRepository dishRepository;

    @Override
    public void createMealTemplate(MealTemplateDto mealTemplateDto) {
        // Check if template already exists
        if (mealTemplateRepository.existsByName(mealTemplateDto.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Meal template already exists with name: " + mealTemplateDto.getName());
        }

        MealTemplate mealTemplate = MealTemplateMapper.mapToMealTemplate(
                mealTemplateDto, new MealTemplate());
        mealTemplateRepository.save(mealTemplate);
    }

    @Override
    public MealTemplateDto fetchMealTemplate(String id) {
        MealTemplate mealTemplate = mealTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MealTemplate", "id", id));

        return MealTemplateMapper.mapToMealTemplateDto(mealTemplate, new MealTemplateDto());
    }

    @Override
    public List<MealTemplateDto> fetchAllMealTemplates() {
        List<MealTemplate> templates = mealTemplateRepository.findAll();
        return templates.stream()
                .map(template -> MealTemplateMapper.mapToMealTemplateDto(template, new MealTemplateDto()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateDishSelection(String templateId, List<String> dishIds) {
        MealTemplate template = mealTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("MealTemplate", "id", templateId));

        // Fetch selected dishes
        List<Dish> dishes = dishIds.stream()
                .map(id -> dishRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Dish", "id", id)))
                .collect(Collectors.toList());

        // Validate using template's validation logic
        return template.validateMeal(dishes);
    }
}