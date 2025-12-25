package com.foodopia.meal.entity;

import com.foodopia.meal.domain.IPriceCalculable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "meals")
@Data
public class Meal implements IPriceCalculable {
    @Id
    private String id;

    @Field("name")
    @Indexed
    private String name;

    @DBRef
    private List<Dish> dishes;

    @DBRef
    @Indexed
    private MealTemplate template;

    @Field("serving_date")
    @Indexed
    private LocalDate servingDate;

    // Constructor with validation
    public Meal(String id, String name, List<Dish> dishes, MealTemplate template, LocalDate servingDate) {
        this.id = id;
        this.name = name;
        this.dishes = dishes;
        this.template = template;
        this.servingDate = servingDate;

        // Validate meal against template
        if (!template.validateMeal(dishes)) {
            throw new IllegalArgumentException("Meal does not match template requirements");
        }
    }

    @Override
    public double calculateCost() {
        return 0;
    }

    @Override
    public double calculatePrice(double markup) {
        return 0;
    }
}
