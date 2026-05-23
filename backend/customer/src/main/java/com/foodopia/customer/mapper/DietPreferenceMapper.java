package com.foodopia.customer.mapper;

import com.foodopia.customer.dto.DietPreferenceDto;
import com.foodopia.customer.entity.DietPreference;

public final class DietPreferenceMapper {

    private DietPreferenceMapper() {}

    public static DietPreferenceDto mapToDto(DietPreference preference, DietPreferenceDto dto) {
        if (preference == null) {
            return null;
        }
        dto.setSavory(preference.getSavory());
        dto.setDietaryGoals(preference.getDietaryGoals());
        dto.setAllergies(preference.getAllergies());
        dto.setFoodsToAvoid(preference.getFoodsToAvoid());
        return dto;
    }

    public static DietPreference mapToEntity(DietPreferenceDto dto, DietPreference preference) {
        if (dto == null) {
            return null;
        }
        preference.setSavory(dto.getSavory());
        preference.setDietaryGoals(dto.getDietaryGoals());
        preference.setAllergies(dto.getAllergies());
        preference.setFoodsToAvoid(dto.getFoodsToAvoid());
        return preference;
    }
}
