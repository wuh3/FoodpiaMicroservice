package com.foodopia.meal.repository;

import com.foodopia.meal.entity.MealCustomization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealCustomizationRepository extends MongoRepository<MealCustomization, String> {

    // Find by scheduled meal ID
    Optional<MealCustomization> findByScheduledMealId(String scheduledMealId);

    // Find customizations by user ID
    List<MealCustomization> findByUserId(String userId);

    // Find customizations by user and delivery date
    List<MealCustomization> findByUserIdAndDeliveryDate(String userId, LocalDate deliveryDate);

    // Find customizations by delivery date
    List<MealCustomization> findByDeliveryDate(LocalDate deliveryDate);

    // Find customizations within date range
    List<MealCustomization> findByDeliveryDateBetween(LocalDate startDate, LocalDate endDate);

    // Check if customization exists for scheduled meal
    boolean existsByScheduledMealId(String scheduledMealId);
}