package com.foodopia.customer.service;

import com.foodopia.customer.dto.DietPreferenceDto;
import com.foodopia.customer.dto.UserProfileDto;

public interface IUserProfileService {

    void createProfile(UserProfileDto profileDto);

    UserProfileDto fetchProfileByUserId(String userId);

    boolean updateProfile(String userId, UserProfileDto profileDto);

    boolean updateDietPreference(String userId, DietPreferenceDto dietPreferenceDto);
}
