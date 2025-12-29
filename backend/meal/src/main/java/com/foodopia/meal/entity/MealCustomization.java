package com.foodopia.meal.entity;

import com.foodopia.meal.domain.IPriceCalculable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "meal_customizations")
@CompoundIndexes({
        @CompoundIndex(name = "user_delivery_idx", def = "{'user_id': 1, 'delivery_date': 1}"),
        @CompoundIndex(name = "delivery_prep_idx", def = "{'delivery_date': 1, 'preparation_status': 1}"),
        @CompoundIndex(name = "customization_status_idx", def = "{'customization_status': 1, 'delivery_date': 1}")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealCustomization implements IPriceCalculable {

    @Id
    private String id;

    // ==================== Cross-Service References ====================
    @Field("scheduled_meal_id")
    @Indexed(unique = true)
    private String scheduledMealId;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("delivery_date")
    @Indexed
    private LocalDate deliveryDate;

    // ==================== Meal Configuration ====================

    @Field("meal_template_id")
    private String mealTemplateId;

    @DBRef
    @Field("selected_dishes")
    private List<Dish> selectedDishes = new ArrayList<>();

    // ==================== Pricing ====================

    @Field("total_cost")
    private double totalCost;

    @Field("total_price")
    private double totalPrice;

    // ==================== Timestamps ====================

    /**
     * When this customization record was created
     */
    @Field("created_at")
    private LocalDateTime createdAt;

    /**
     * Last time this record was modified
     */
    @Field("updated_at")
    private LocalDateTime updatedAt;
    // ==================== Business Logic Methods ====================

    @Override
    public double calculateCost() {
        if (selectedDishes == null || selectedDishes.isEmpty()) {
            return 0.0;
        }
        return selectedDishes.stream()
                .mapToDouble(Dish::calculateCost)
                .sum();
    }

    @Override
    public double calculatePrice(double markup) {
        return calculateCost() * (1 + markup);
    }
}