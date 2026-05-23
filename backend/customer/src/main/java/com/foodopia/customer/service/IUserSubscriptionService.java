package com.foodopia.customer.service;

public interface IUserSubscriptionService {

    void pauseSubscription(String subscriptionId);

    void resumeSubscription(String subscriptionId);

    void cancelSubscription(String subscriptionId);
}
