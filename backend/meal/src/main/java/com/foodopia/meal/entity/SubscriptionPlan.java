package com.foodopia.meal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "subscription_plans")
public class SubscriptionPlan {
    @Id
    private String id;

    @Field("name")
    @Indexed(unique = true)
    private String name; // "Basic 5-Day", "Premium 7-Day"

    private String description;

    // Plan configuration
    private int mealsPerWeek;

    @Field("meal_template_id")
    private String mealTemplateId; // Reference to MealTemplate

    // Business rules
    private int advanceCustomizationDays; // Days before delivery
    private int minimumSubscriptionWeeks;

    @Field("is_active")
    private boolean isActive;

    @Field("created_at")
    private LocalDateTime createdAt;
}