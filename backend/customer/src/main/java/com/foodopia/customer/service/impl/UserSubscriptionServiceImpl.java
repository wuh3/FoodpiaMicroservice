package com.foodopia.customer.service.impl;

import com.foodopia.customer.entity.UserSubscription;
import com.foodopia.customer.entity.enums.SubscriptionStatus;
import com.foodopia.customer.exception.InvalidSubscriptionStateException;
import com.foodopia.customer.exception.ResourceNotFoundException;
import com.foodopia.customer.repository.UserSubscriptionRepository;
import com.foodopia.customer.service.IUserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements IUserSubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;

    @Override
    public void pauseSubscription(String subscriptionId) {
        UserSubscription subscription = findSubscription(subscriptionId);
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new InvalidSubscriptionStateException(
                    "Only ACTIVE subscriptions can be paused; current status is " + subscription.getStatus());
        }
        subscription.setStatus(SubscriptionStatus.PAUSED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void resumeSubscription(String subscriptionId) {
        UserSubscription subscription = findSubscription(subscriptionId);
        if (subscription.getStatus() != SubscriptionStatus.PAUSED) {
            throw new InvalidSubscriptionStateException(
                    "Only PAUSED subscriptions can be resumed; current status is " + subscription.getStatus());
        }
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void cancelSubscription(String subscriptionId) {
        UserSubscription subscription = findSubscription(subscriptionId);
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new InvalidSubscriptionStateException("Subscription is already cancelled");
        }
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }

    private UserSubscription findSubscription(String subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSubscription", "id", subscriptionId));
    }
}
