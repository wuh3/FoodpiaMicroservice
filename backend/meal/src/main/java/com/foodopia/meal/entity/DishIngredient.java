package com.foodopia.meal.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class DishIngredient {
    private String ingredientId;
    private double quantity;
}
