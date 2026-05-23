package com.foodopia.customer.repository;

import com.foodopia.customer.entity.UserSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends MongoRepository<UserSubscription, String> {
}
