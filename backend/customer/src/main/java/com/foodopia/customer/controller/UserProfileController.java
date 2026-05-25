package com.foodopia.customer.controller;

import com.foodopia.customer.constants.CustomerConstants;
import com.foodopia.customer.dto.DietPreferenceDto;
import com.foodopia.customer.dto.ResponseDto;
import com.foodopia.customer.dto.UserProfileDto;
import com.foodopia.customer.service.IUserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    private final IUserProfileService profileService;

    @PostMapping
    public ResponseEntity<ResponseDto> createProfile(@Valid @RequestBody UserProfileDto profileDto) {
        log.debug("Received request to create profile for userId: {}", profileDto.getUserId());
        profileService.createProfile(profileDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CustomerConstants.STATUS_201, CustomerConstants.MESSAGE_201));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileDto> fetchProfile(@PathVariable String userId) {
        log.debug("Received request to fetch profile for userId: {}", userId);
        return ResponseEntity.ok(profileService.fetchProfileByUserId(userId));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ResponseDto> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileDto profileDto) {
        log.debug("Received request to update profile for userId: {}", userId);
        boolean updated = profileService.updateProfile(userId, profileDto);
        if (updated) {
            return ResponseEntity.ok(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseDto(CustomerConstants.STATUS_417, CustomerConstants.MESSAGE_417_UPDATE));
    }

    @PutMapping("/user/{userId}/diet-preference")
    public ResponseEntity<ResponseDto> updateDietPreference(
            @PathVariable String userId,
            @Valid @RequestBody DietPreferenceDto dietPreferenceDto) {
        log.debug("Received request to update diet preference for userId: {}", userId);
        boolean updated = profileService.updateDietPreference(userId, dietPreferenceDto);
        if (updated) {
            return ResponseEntity.ok(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
        }
        return ResponseEntity
                .status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseDto(CustomerConstants.STATUS_417, CustomerConstants.MESSAGE_417_UPDATE));
    }
}
