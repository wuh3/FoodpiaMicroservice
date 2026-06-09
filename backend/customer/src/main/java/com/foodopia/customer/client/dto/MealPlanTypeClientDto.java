package com.foodopia.customer.client.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MealPlanTypeClientDto {

    private String id;
    private String planCode;
    private String displayName;
    private String description;
    private String templateId;
    private List<PlanLevelClientDto> levels = new ArrayList<>();
    private boolean isActive;
}
