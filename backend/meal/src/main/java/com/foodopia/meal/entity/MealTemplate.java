package com.foodopia.meal.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "meal_templates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class MealTemplate {
    @Id
    private String id;
    @Field("name")
    @Indexed(unique = true)
    private String name;
    private Map<String, Integer> dishCategories;
    private int totalDishes;

    @Field("required_tags")
    private List<String> requiredTags = new ArrayList<>();

    @Field("forbidden_tags")
    private List<String> forbiddenTags = new ArrayList<>();

    // Validate if a meal matches this template
    public boolean validateMeal(List<Dish> dishes) {
        if (dishes.size() != totalDishes) return false;

        Map<String, Long> categoryCounts = dishes.stream()
                .collect(Collectors.groupingBy(Dish::getCategory, Collectors.counting()));

        for (Map.Entry<String, Integer> entry : dishCategories.entrySet()) {
            String category = entry.getKey();
            Integer requiredCount = entry.getValue();
            Long actualCount = categoryCounts.getOrDefault(category, 0L);

            if (!actualCount.equals(requiredCount.longValue())) {
                return false;
            }
        }

        List<String> required = requiredTags != null ? requiredTags : List.of();
        List<String> forbidden = forbiddenTags != null ? forbiddenTags : List.of();
        for (Dish dish : dishes) {
            List<String> tags = dish.getDietaryTags() != null ? dish.getDietaryTags() : List.of();
            if (!tags.containsAll(required)) return false;
            if (tags.stream().anyMatch(forbidden::contains)) return false;
        }

        return true;
    }
}
