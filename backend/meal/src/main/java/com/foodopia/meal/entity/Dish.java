package com.foodopia.meal.entity;

import com.foodopia.meal.domain.IPriceCalculable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "dishes")
@CompoundIndexes({
        @CompoundIndex(name = "category_available_idx", def = "{'category': 1, 'is_available': 1}"),
        @CompoundIndex(name = "available_date_range_idx", def = "{'available_from': 1, 'available_until': 1}")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish implements IPriceCalculable {
    @Id
    private String id;

    @Field("name")
    @Indexed(unique = true)
    private String name;

    @Field("description")
    private String description;

    @Field("ingredients")
    private List<DishIngredient> ingredients = new ArrayList<>();

    @Field("category")
    @Indexed
    private String category; // "meat", "vegetable", "soup", "grain", "dessert"

    @Field("serving_size")
    private int servingSize; // in grams

    // Availability
    @Field("is_available")
    @Indexed
    private boolean isAvailable;

    @Field("available_from")
    private LocalDate availableFrom;

    @Field("available_until")
    private LocalDate availableUntil; // null for always available

    // Dietary tags
    @Field("dietary_tags")
    private List<String> dietaryTags = new ArrayList<>(); // "vegan", "halal", "gluten-free"

    // Allergen information
    @Field("allergens")
    private List<String> allergens = new ArrayList<>(); // "peanuts", "dairy", "shellfish"

    // Media
    @Field("image_url")
    private String imageUrl;

    // Popularity metrics
    @Field("popularity_score")
    private double popularityScore; // For recommendations

    @Field("times_ordered")
    private int timesOrdered;

    // Timestamps
    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Override
    public double calculateCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(DishIngredient::calculateCost)
                .sum();
    }

    @Override
    public double calculatePrice(double markup) {
        return calculateCost() * (1 + markup);
    }

    // Get ingredients by category
    public List<DishIngredient> getIngredientsByCategory(String category) {
        return ingredients.stream()
                .filter(di -> di.getIngredient().getCategory().equals(category))
                .collect(Collectors.toList());
    }
}