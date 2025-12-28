package com.foodopia.meal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.foodopia.meal.domain.IPriceCalculable;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import java.util.ArrayList;

@Document(collection = "scheduled_meals")
@CompoundIndexes({
        @CompoundIndex(name = "user_delivery_idx", def = "{'user_id': 1, 'delivery_date': 1}"),
        @CompoundIndex(name = "subscription_delivery_idx", def = "{'subscription_id': 1, 'delivery_date': 1}"),
        @CompoundIndex(name = "delivery_preparation_idx", def = "{'delivery_date': 1, 'preparation_status': 1}"),
        @CompoundIndex(name = "customization_deadline_idx", def = "{'customization_deadline': 1, 'customization_status': 1}")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledMeal implements IPriceCalculable {
    @Id
    private String id;

    // Cross-service references (customer-service)
    @Field("user_id")
    @Indexed
    private String userId;

    @Field("subscription_id")
    @Indexed
    private String subscriptionId;

    // Delivery information
    @Field("delivery_date")
    @Indexed
    private LocalDate deliveryDate;

    @Field("delivery_time_slot")
    private String deliveryTimeSlot; // "9AM-12PM", "12PM-3PM", "3PM-6PM"

    // Delivery address (cached from customer-service for kitchen use)
    @Field("delivery_address")
    private DeliveryAddressCache deliveryAddress;

    // Meal configuration
    @Field("meal_template_id")
    private String mealTemplateId;

    @DBRef
    @Field("selected_dishes")
    private List<Dish> selectedDishes = new ArrayList<>();

    // Customization tracking
    @Field("customization_status")
    private CustomizationStatus customizationStatus;

    @Field("customization_deadline")
    private LocalDateTime customizationDeadline;

    @Field("is_default")
    private boolean isDefault; // True if system-assigned, false if user-customized

    // Kitchen/Preparation status (NOT delivery status)
    @Field("preparation_status")
    @Indexed
    private PreparationStatus preparationStatus;

    @Field("kitchen_notes")
    private String kitchenNotes;

    @Field("special_instructions")
    private String specialInstructions; // From customer

    // Pricing
    @Field("total_price")
    private double totalPrice;

    // Cross-service reference (delivery-service)
    @Field("delivery_id")
    private String deliveryId; // Reference to delivery in delivery-service

    // Timestamps
    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("prepared_at")
    private LocalDateTime preparedAt;

    @Field("handed_to_delivery_at")
    private LocalDateTime handedToDeliveryAt;

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

// Cached delivery address for kitchen operations
@Data
@NoArgsConstructor
@AllArgsConstructor
class DeliveryAddressCache {
    private String street;
    private String city;
    private String province;
    private String postalCode;
    private String deliveryInstructions;

    @Field("cached_at")
    private LocalDateTime cachedAt;
}

// Customization status
enum CustomizationStatus {
    PENDING,           // Awaiting user customization
    CUSTOMIZED,        // User has customized
    LOCKED,            // Past customization deadline
    DEFAULT_ASSIGNED   // System assigned default dishes
}

// Kitchen preparation status
enum PreparationStatus {
    SCHEDULED,              // Meal is scheduled for future
    PREPARING,              // Kitchen is preparing
    READY_FOR_DELIVERY,     // Ready to be picked up by delivery
    HANDED_TO_DELIVERY,     // Handed over to delivery service
    COMPLETED,              // Delivery confirmed (via event from delivery-service)
    CANCELLED               // Cancelled (subscription cancelled or skipped)
}