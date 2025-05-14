package com.foodopia.authentication.repository;

import com.foodopia.authentication.entity.KitchenUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface KitchenUserRepository extends MongoRepository<KitchenUser, String> {
    Optional<KitchenUser> findByEmail(String email);
    Optional<KitchenUser> findByUsername(String username);
}
