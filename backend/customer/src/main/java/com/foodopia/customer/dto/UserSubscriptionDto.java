package com.foodopia.customer.dto;

import com.foodopia.customer.entity.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserSubscriptionDto {

    private String id;

    @NotBlank(message = "userId cannot be null or empty")
    private String userId;

    @NotBlank(message = "planName cannot be null or empty")
    private String planName;

    private SubscriptionStatus status;

    @NotNull(message = "startDate cannot be null")
    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
