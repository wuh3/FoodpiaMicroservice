package com.foodopia.meal.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

@Service
@AllArgsConstructor
public class MealTemplateServiceImpl implements IMealTemplateService {

    private static final Logger log = LoggerFactory.getLogger(MealTemplateServiceImpl.class);
    private MealTemplateRepository mealTemplateRepository;
    private DishRepository dishRepository;

    @Override
    public void createMealTemplate(MealTemplateDto mealTemplateDto) {
        log.debug("Creating meal template with name: {}", mealTemplateDto.getName());
        // Check if template already exists
        if (mealTemplateRepository.existsByName(mealTemplateDto.getName())) {
            log.warn("Attempted to create meal template that already exists: {}", mealTemplateDto.getName());
            throw new ResourceAlreadyExistsException(
                    "Meal template already exists with name: " + mealTemplateDto.getName());
        }

        MealTemplate mealTemplate = MealTemplateMapper.mapToMealTemplate(
                mealTemplateDto, new MealTemplate());
        mealTemplateRepository.save(mealTemplate);
        log.debug("Successfully created meal template with id: {} and name: {}", mealTemplate.getId(), mealTemplate.getName());
    }

    @Override
    public MealTemplateDto fetchMealTemplate(String id) {
        log.debug("Fetching meal template with id: {}", id);
        MealTemplate mealTemplate = mealTemplateRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Meal template not found with id: {}", id);
                    return new ResourceNotFoundException("MealTemplate", "id", id);
                });

        log.debug("Successfully fetched meal template with id: {} and name: {}", id, mealTemplate.getName());
        return MealTemplateMapper.mapToMealTemplateDto(mealTemplate, new MealTemplateDto());
    }

    @Override
    public List<MealTemplateDto> fetchAllMealTemplates() {
        log.debug("Fetching all meal templates");
        List<MealTemplate> templates = mealTemplateRepository.findAll();
        log.debug("Found {} meal templates", templates.size());
        return templates.stream()
                .map(template -> MealTemplateMapper.mapToMealTemplateDto(template, new MealTemplateDto()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateDishSelection(String templateId, List<String> dishIds) {
        log.debug("Validating dish selection for template id: {} with {} dishes", templateId, dishIds.size());
        MealTemplate template = mealTemplateRepository.findById(templateId)
                .orElseThrow(() -> {
                    log.error("Meal template not found for validation with id: {}", templateId);
                    return new ResourceNotFoundException("MealTemplate", "id", templateId);
                });

        // Fetch selected dishes
        List<Dish> dishes = dishIds.stream()
                .map(id -> dishRepository.findById(id)
                        .orElseThrow(() -> {
                            log.error("Dish not found during validation with id: {}", id);
                            return new ResourceNotFoundException("Dish", "id", id);
                        }))
                .collect(Collectors.toList());

        // Validate using template's validation logic
        boolean isValid = template.validateMeal(dishes);
        log.debug("Dish selection validation result for template id: {} is: {}", templateId, isValid);
        return isValid;
    }
}