package com.foodopia.customer.repository;

import com.foodopia.customer.entity.UserSubscription;
import com.foodopia.customer.entity.enums.SubscriptionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends MongoRepository<UserSubscription, String> {

    List<UserSubscription> findByUserId(String userId);

    List<UserSubscription> findByPlanCode(String planCode);

    List<UserSubscription> findByUserIdAndStatus(String userId, SubscriptionStatus status);
}
