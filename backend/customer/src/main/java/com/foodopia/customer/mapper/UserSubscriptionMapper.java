package com.foodopia.customer.mapper;

import com.foodopia.customer.dto.UserSubscriptionDto;
import com.foodopia.customer.entity.UserSubscription;

public final class UserSubscriptionMapper {

    private UserSubscriptionMapper() {}

    public static UserSubscriptionDto mapToDto(UserSubscription subscription, UserSubscriptionDto dto) {
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUserId());
        dto.setPlanName(subscription.getPlanName());
        dto.setPlanCode(subscription.getPlanCode());
        dto.setPlanLevel(subscription.getPlanLevel());
        dto.setMealsPerMonth(subscription.getMealsPerMonth());
        dto.setStatus(subscription.getStatus());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        return dto;
    }

    public static UserSubscription mapToEntity(UserSubscriptionDto dto, UserSubscription subscription) {
        subscription.setUserId(dto.getUserId());
        subscription.setPlanName(dto.getPlanName());
        subscription.setPlanCode(dto.getPlanCode());
        subscription.setPlanLevel(dto.getPlanLevel());
        subscription.setMealsPerMonth(dto.getMealsPerMonth());
        subscription.setStatus(dto.getStatus());
        subscription.setStartDate(dto.getStartDate());
        subscription.setEndDate(dto.getEndDate());
        return subscription;
    }
}
