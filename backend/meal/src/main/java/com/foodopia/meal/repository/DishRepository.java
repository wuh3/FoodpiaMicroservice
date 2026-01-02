package com.foodopia.meal.repository;

import com.foodopia.meal.entity.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends MongoRepository<Dish, String> {

    // Find dish by name
    Optional<Dish> findByName(String name);

    // Find dishes by category
    List<Dish> findByCategory(String category);

    // Check if dish exists by name
    boolean existsByName(String name);
}