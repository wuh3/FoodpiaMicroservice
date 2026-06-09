/**
 * Implicitly used to distinguish the number of meals within a type of meal plan
 */
package com.foodopia.meal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanLevel {

    @Field("level")
    private int level;

    @Field("meals_per_month")
    private int mealsPerMonth;

    @Field("monthly_price")
    private double monthlyPrice;
}
