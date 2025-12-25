package com.foodopia.meal.entity;

import com.foodopia.meal.domain.IPriceCalculable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "dishes")
@AllArgsConstructor
@Getter
@Setter
public class Dish implements IPriceCalculable {
    @Id
    private String id;
    @Field("name")
    @Indexed(unique = true)
    private String name;
    private List<DishIngredient> ingredients = new ArrayList<>();
    @Field("category")
    @Indexed
    private String category;
    @Field("serving_size")
    @Indexed
    private int servingSize;

    @Override
    public double calculateCost() {
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
