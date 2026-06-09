package com.foodopia.meal.service.impl;

import com.foodopia.meal.dto.MealPlanTypeDto;
import com.foodopia.meal.entity.MealPlanType;
import com.foodopia.meal.exception.ResourceAlreadyExistsException;
import com.foodopia.meal.exception.ResourceNotFoundException;
import com.foodopia.meal.mapper.MealPlanTypeMapper;
import com.foodopia.meal.repository.MealPlanTypeRepository;
import com.foodopia.meal.repository.MealTemplateRepository;
import com.foodopia.meal.service.IMealPlanTypeService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MealPlanTypeServiceImpl implements IMealPlanTypeService {

    private static final Logger log = LoggerFactory.getLogger(MealPlanTypeServiceImpl.class);

    private MealPlanTypeRepository mealPlanTypeRepository;
    private MealTemplateRepository mealTemplateRepository;

    @Override
    public void createMealPlanType(MealPlanTypeDto mealPlanTypeDto) {
        log.debug("Creating meal plan type with planCode: {}", mealPlanTypeDto.getPlanCode());

        if (mealPlanTypeRepository.existsByPlanCode(mealPlanTypeDto.getPlanCode())) {
            throw new ResourceAlreadyExistsException(
                    "Meal plan type already exists with planCode: " + mealPlanTypeDto.getPlanCode());
        }

        validateTemplateExists(mealPlanTypeDto.getTemplateId());

        MealPlanType mealPlanType = MealPlanTypeMapper.mapToEntity(mealPlanTypeDto, new MealPlanType());
        mealPlanTypeRepository.save(mealPlanType);
        log.debug("Successfully created meal plan type with planCode: {}", mealPlanType.getPlanCode());
    }

    @Override
    public MealPlanTypeDto fetchMealPlanType(String planCode) {
        log.debug("Fetching meal plan type with planCode: {}", planCode);
        MealPlanType mealPlanType = findByPlanCode(planCode);
        return MealPlanTypeMapper.mapToDto(mealPlanType, new MealPlanTypeDto());
    }

    @Override
    public List<MealPlanTypeDto> fetchAllActiveMealPlanTypes() {
        log.debug("Fetching all active meal plan types");
        return mealPlanTypeRepository.findByIsActiveTrue().stream()
                .map(planType -> MealPlanTypeMapper.mapToDto(planType, new MealPlanTypeDto()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateMealPlanType(String planCode, MealPlanTypeDto mealPlanTypeDto) {
        log.debug("Updating meal plan type with planCode: {}", planCode);
        MealPlanType mealPlanType = findByPlanCode(planCode);

        if (mealPlanTypeDto.getTemplateId() != null) {
            validateTemplateExists(mealPlanTypeDto.getTemplateId());
            mealPlanType.setTemplateId(mealPlanTypeDto.getTemplateId());
        }
        if (mealPlanTypeDto.getDisplayName() != null) {
            mealPlanType.setDisplayName(mealPlanTypeDto.getDisplayName());
        }
        if (mealPlanTypeDto.getDescription() != null) {
            mealPlanType.setDescription(mealPlanTypeDto.getDescription());
        }
        if (mealPlanTypeDto.getLevels() != null && !mealPlanTypeDto.getLevels().isEmpty()) {
            mealPlanType.setLevels(MealPlanTypeMapper.mapLevelsToEntity(mealPlanTypeDto.getLevels()));
        }
        mealPlanType.setActive(mealPlanTypeDto.isActive());

        mealPlanTypeRepository.save(mealPlanType);
        return true;
    }

    private MealPlanType findByPlanCode(String planCode) {
        return mealPlanTypeRepository.findByPlanCode(planCode)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlanType", "planCode", planCode));
    }

    private void validateTemplateExists(String templateId) {
        if (!mealTemplateRepository.existsById(templateId)) {
            throw new ResourceNotFoundException("MealTemplate", "id", templateId);
        }
    }
}
