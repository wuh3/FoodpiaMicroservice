package com.foodopia.customer.service.impl;

import com.foodopia.customer.dto.DietPreferenceDto;
import com.foodopia.customer.dto.UserProfileDto;
import com.foodopia.customer.entity.DietPreference;
import com.foodopia.customer.entity.UserProfile;
import com.foodopia.customer.exception.ResourceAlreadyExistsException;
import com.foodopia.customer.exception.ResourceNotFoundException;
import com.foodopia.customer.mapper.DietPreferenceMapper;
import com.foodopia.customer.mapper.UserProfileMapper;
import com.foodopia.customer.repository.UserProfileRepository;
import com.foodopia.customer.service.IUserProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements IUserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository profileRepository;

    @Override
    public void createProfile(UserProfileDto profileDto) {
        log.debug("Creating profile for userId: {}", profileDto.getUserId());
        if (profileRepository.existsByUserId(profileDto.getUserId())) {
            throw new ResourceAlreadyExistsException(
                    "UserProfile already exists for userId: " + profileDto.getUserId());
        }
        UserProfile profile = UserProfileMapper.mapToEntity(profileDto, UserProfile.builder().build());
        profileRepository.save(profile);
        log.debug("Created profile with id: {} for userId: {}", profile.getId(), profile.getUserId());
    }

    @Override
    public UserProfileDto fetchProfileByUserId(String userId) {
        log.debug("Fetching profile for userId: {}", userId);
        UserProfile profile = findByUserId(userId);
        return UserProfileMapper.mapToDto(profile, new UserProfileDto());
    }

    @Override
    public boolean updateProfile(String userId, UserProfileDto profileDto) {
        log.debug("Updating personal info for userId: {}", userId);
        UserProfile profile = findByUserId(userId);
        profile.setProfilePicUrl(profileDto.getProfilePicUrl());
        profile.setLegalName(profileDto.getLegalName());
        profile.setNickname(profileDto.getNickname());
        profile.setPhone(profileDto.getPhone());
        profileRepository.save(profile);
        return true;
    }

    @Override
    public boolean updateDietPreference(String userId, DietPreferenceDto dietPreferenceDto) {
        log.debug("Updating diet preference for userId: {}", userId);
        UserProfile profile = findByUserId(userId);
        DietPreference preference = profile.getDietPreference() != null
                ? profile.getDietPreference()
                : new DietPreference();
        profile.setDietPreference(DietPreferenceMapper.mapToEntity(dietPreferenceDto, preference));
        profileRepository.save(profile);
        return true;
    }

    private UserProfile findByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", userId));
    }
}
