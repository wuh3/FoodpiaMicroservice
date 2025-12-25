package com.foodopia.meal.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.foodopia.meal.entity.Dish;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "meal_templates")
@Data
@AllArgsConstructor
@Getter @Setter
public class MealTemplate {
    @Id
    private String id;
    @Field("name")
    @Indexed(unique = true)
    private String name;
    private Map<String, Integer> dishCategories;
    private int totalDishes;

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

        return true;
    }
}
