package com.foodopia.customer.service;

import com.foodopia.customer.dto.UserSubscriptionDto;

import java.util.List;

public interface IUserSubscriptionService {

    void createSubscription(UserSubscriptionDto subscriptionDto);

    UserSubscriptionDto fetchSubscription(String subscriptionId);

    List<UserSubscriptionDto> fetchSubscriptionsByUserId(String userId);

    List<UserSubscriptionDto> fetchSubscriptionsByPlanCode(String planCode);

    List<UserSubscriptionDto> fetchActiveSubscriptionsByUserId(String userId);

    boolean updateSubscription(UserSubscriptionDto subscriptionDto);

    void pauseSubscription(String subscriptionId);

    void resumeSubscription(String subscriptionId);

    void cancelSubscription(String subscriptionId);
}
