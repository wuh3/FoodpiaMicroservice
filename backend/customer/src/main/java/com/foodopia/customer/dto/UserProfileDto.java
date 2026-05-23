package com.foodopia.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileDto {

    private String id;

    @NotBlank(message = "userId cannot be null or empty")
    private String userId;

    private String profilePicUrl;

    private String legalName;

    private String nickname;

    private String phone;

    @Valid
    private DietPreferenceDto dietPreference;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
