package com.foodopia.meal.repository;

import com.foodopia.meal.entity.MealPlanType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MealPlanTypeRepository extends MongoRepository<MealPlanType, String> {

    Optional<MealPlanType> findByPlanCode(String planCode);

    boolean existsByPlanCode(String planCode);

    List<MealPlanType> findByIsActiveTrue();
}
