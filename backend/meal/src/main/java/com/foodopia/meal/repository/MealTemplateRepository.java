package com.foodopia.meal.repository;

import com.foodopia.meal.entity.MealTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealTemplateRepository extends MongoRepository<MealTemplate, String> {

    // Find template by name
    Optional<MealTemplate> findByName(String name);

    // Check if template exists by name
    boolean existsByName(String name);
}