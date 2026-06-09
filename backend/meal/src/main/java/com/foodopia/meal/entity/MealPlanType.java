package com.foodopia.meal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "meal_plan_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanType {

    @Id
    private String id;

    @Field("plan_code")
    @Indexed(unique = true)
    private String planCode;

    @Field("display_name")
    private String displayName;

    @Field("description")
    private String description;

    @Field("template_id")
    private String templateId;

    @Field("levels")
    private List<PlanLevel> levels = new ArrayList<>();

    @Field("is_active")
    private boolean isActive;
}
