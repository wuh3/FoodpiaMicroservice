package com.foodopia.customer.service.impl;

import com.foodopia.customer.dto.UserSubscriptionDto;
import com.foodopia.customer.entity.UserSubscription;
import com.foodopia.customer.entity.enums.SubscriptionStatus;
import com.foodopia.customer.exception.InvalidSubscriptionStateException;
import com.foodopia.customer.exception.ResourceNotFoundException;
import com.foodopia.customer.mapper.UserSubscriptionMapper;
import com.foodopia.customer.repository.UserSubscriptionRepository;
import com.foodopia.customer.service.IUserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements IUserSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(UserSubscriptionServiceImpl.class);

    private final UserSubscriptionRepository subscriptionRepository;

    @Override
    public void createSubscription(UserSubscriptionDto subscriptionDto) {
        log.debug("Creating subscription for userId: {}", subscriptionDto.getUserId());
        UserSubscription subscription = UserSubscriptionMapper.mapToEntity(
                subscriptionDto, UserSubscription.builder().build());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionRepository.save(subscription);
        log.debug("Created subscription with id: {}", subscription.getId());
    }

    @Override
    public UserSubscriptionDto fetchSubscription(String subscriptionId) {
        log.debug("Fetching subscription with id: {}", subscriptionId);
        UserSubscription subscription = findSubscription(subscriptionId);
        return UserSubscriptionMapper.mapToDto(subscription, new UserSubscriptionDto());
    }

    @Override
    public List<UserSubscriptionDto> fetchSubscriptionsByUserId(String userId) {
        log.debug("Fetching subscriptions for userId: {}", userId);
        return subscriptionRepository.findByUserId(userId).stream()
                .map(sub -> UserSubscriptionMapper.mapToDto(sub, new UserSubscriptionDto()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateSubscription(UserSubscriptionDto subscriptionDto) {
        log.debug("Updating subscription with id: {}", subscriptionDto.getId());
        UserSubscription subscription = findSubscription(subscriptionDto.getId());
        subscription.setPlanName(subscriptionDto.getPlanName());
        subscription.setStartDate(subscriptionDto.getStartDate());
        subscription.setEndDate(subscriptionDto.getEndDate());
        subscriptionRepository.save(subscription);
        return true;
    }

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
