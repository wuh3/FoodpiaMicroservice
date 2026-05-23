package com.foodopia.customer.mapper;

import com.foodopia.customer.dto.DietPreferenceDto;
import com.foodopia.customer.dto.UserProfileDto;
import com.foodopia.customer.entity.DietPreference;
import com.foodopia.customer.entity.UserProfile;

public final class UserProfileMapper {

    private UserProfileMapper() {}

    public static UserProfileDto mapToDto(UserProfile profile, UserProfileDto dto) {
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setProfilePicUrl(profile.getProfilePicUrl());
        dto.setLegalName(profile.getLegalName());
        dto.setNickname(profile.getNickname());
        dto.setPhone(profile.getPhone());
        if (profile.getDietPreference() != null) {
            dto.setDietPreference(
                    DietPreferenceMapper.mapToDto(profile.getDietPreference(), new DietPreferenceDto()));
        }
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }

    public static UserProfile mapToEntity(UserProfileDto dto, UserProfile profile) {
        profile.setUserId(dto.getUserId());
        profile.setProfilePicUrl(dto.getProfilePicUrl());
        profile.setLegalName(dto.getLegalName());
        profile.setNickname(dto.getNickname());
        profile.setPhone(dto.getPhone());
        if (dto.getDietPreference() != null) {
            DietPreference preference = profile.getDietPreference() != null
                    ? profile.getDietPreference()
                    : new DietPreference();
            profile.setDietPreference(DietPreferenceMapper.mapToEntity(dto.getDietPreference(), preference));
        }
        return profile;
    }
}
