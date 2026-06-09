package com.foodopia.customer.dto;

import com.foodopia.customer.entity.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserSubscriptionDto {

    private String id;

    @NotBlank(message = "userId cannot be null or empty")
    private String userId;

    private String planName;

    @NotBlank(message = "planCode cannot be null or empty")
    private String planCode;

    @Positive(message = "planLevel must be greater than zero")
    private int planLevel;

    private int mealsPerMonth;

    private SubscriptionStatus status;

    @NotNull(message = "startDate cannot be null")
    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
