package com.foodopia.customer.dto;

import com.foodopia.customer.entity.enums.SavoryIntensity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DietPreferenceDto {

    private SavoryIntensity savory;

    private List<String> dietaryGoals = new ArrayList<>();

    private List<String> allergies = new ArrayList<>();

    private List<String> foodsToAvoid = new ArrayList<>();
}
