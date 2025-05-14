package com.foodopia.authentication.repository;

import com.foodopia.authentication.entity.Operator;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OperatorRepository extends MongoRepository<Operator, String> {
    Optional<Operator> findByUsername(String username);
    Optional<Operator> findByEmail(String email);
}
