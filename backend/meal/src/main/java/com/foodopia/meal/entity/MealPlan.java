package com.foodopia.meal.entity;

import com.foodopia.meal.domain.IPriceCalculable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document("meal_plans")
@Data
@AllArgsConstructor
@Getter
@Setter
public class MealPlan implements IPriceCalculable {
    @Id
    private String id;

    private String name;

    @DBRef
    private List<Meal> meals = new ArrayList<>();

    private int mealsPerWeek;
    private int durationWeeks;
    private LocalDate startDate;
    @Override
    public double calculateCost() {
        return 0;
    }

    @Override
    public double calculatePrice(double markup) {
        return 0;
    }
}
