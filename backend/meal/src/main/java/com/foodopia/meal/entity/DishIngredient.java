package com.foodopia.meal.entity;

import com.foodopia.meal.domain.IPriceCalculable;
import lombok.*;

@Data
@AllArgsConstructor
@Getter @Setter
public class DishIngredient implements IPriceCalculable {
    private Ingredient ingredient;
    private double quantity;


    @Override
    public double calculateCost() {
        return ingredient.getUnitPrice() * quantity;
    }

    @Override
    public double calculatePrice(double markup) {
        return calculateCost() * (1 + markup);
    }

    public double getCost() {
        return calculateCost();
    }
}
