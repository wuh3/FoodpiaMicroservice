package com.foodopia.customer.entity;

import com.foodopia.customer.entity.enums.SavoryIntensity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietPreference {

    @Field("savory")
    private SavoryIntensity savory;

    @Field("dietary_goals")
    private List<String> dietaryGoals = new ArrayList<>();

    @Field("allergies")
    private List<String> allergies = new ArrayList<>();

    @Field("foods_to_avoid")
    private List<String> foodsToAvoid = new ArrayList<>();
}
