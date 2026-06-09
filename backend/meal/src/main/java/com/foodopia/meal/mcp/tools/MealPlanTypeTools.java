package com.foodopia.meal.mcp.tools;

import com.foodopia.meal.dto.MealPlanTypeDto;
import com.foodopia.meal.service.IMealPlanTypeService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MealPlanTypeTools {

    private final IMealPlanTypeService mealPlanTypeService;

    @Tool(
            name = "get_meal_plan_type",
            description = "Fetch a meal plan type by planCode, including display name, linked template ID, and subscription levels (meals per month and monthly price)"
    )
    public MealPlanTypeDto getMealPlanType(
            @ToolParam(description = "Plan code, e.g. low_fat_slim_body_plan") String planCode) {
        return mealPlanTypeService.fetchMealPlanType(planCode);
    }

    @Tool(
            name = "list_active_meal_plan_types",
            description = "List all active meal plan types with their subscription levels and linked template IDs"
    )
    public List<MealPlanTypeDto> listActiveMealPlanTypes() {
        return mealPlanTypeService.fetchAllActiveMealPlanTypes();
    }
}
