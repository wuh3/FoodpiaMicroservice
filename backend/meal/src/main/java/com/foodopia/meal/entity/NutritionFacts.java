package com.foodopia.meal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Units:
 * - caloriesKcal: kcal
 * - proteinG, sugarG: grams
 * - saltMg: milligrams
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionFacts {
    @Field("calories_kcal")
    private double caloriesKcal;

    @Field("protein_g")
    private double proteinG;

    @Field("sugar_g")
    private double sugarG;

    @Field("salt_mg")
    private double saltMg;

    public static NutritionFacts zero() {
        return NutritionFacts.builder()
                .caloriesKcal(0.0)
                .proteinG(0.0)
                .sugarG(0.0)
                .saltMg(0.0)
                .build();
    }

    public NutritionFacts addScaled(NutritionFacts other, double scale) {
        if (other == null) return this;
        this.caloriesKcal += other.caloriesKcal * scale;
        this.proteinG += other.proteinG * scale;
        this.sugarG += other.sugarG * scale;
        this.saltMg += other.saltMg * scale;
        return this;
    }
}

