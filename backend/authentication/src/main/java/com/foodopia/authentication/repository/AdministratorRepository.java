package com.foodopia.authentication.repository;

import com.foodopia.authentication.entity.Administrator;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdministratorRepository extends MongoRepository<Administrator, String> {
    Optional<Administrator> findByEmail(String email);
    Optional<Administrator> findByUsername(String username);
}
