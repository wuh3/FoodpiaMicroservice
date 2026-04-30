package com.foodopia.meal.entity;
import com.foodopia.meal.domain.IPriceCalculable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ingredients")
@Data
@Getter @Setter
@AllArgsConstructor
public class Ingredient implements IPriceCalculable {
    @Id
    private String id;
    @Field("name")
    @Indexed(unique = true)
    private String name;
    private double unitPrice;
    @Field("category")
    @Indexed
    private String category;
    private String unit = "g";

    /**
     * Nutrition density per 100g.
     * Dish nutrition is computed from this + DishIngredient.quantity (grams).
     */
    @Field("nutrition_per_100g")
    private NutritionFacts nutritionPer100g;

    @Override
    public double calculateCost() {
        return this.unitPrice;
    }

    @Override
    public double calculatePrice(double markup) {
        return this.unitPrice * (1 + markup);
    }
}
