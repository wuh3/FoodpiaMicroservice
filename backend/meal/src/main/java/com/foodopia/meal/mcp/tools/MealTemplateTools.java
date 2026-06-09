package com.foodopia.meal.mcp.tools;

import com.foodopia.meal.dto.MealTemplateDto;
import com.foodopia.meal.service.IMealTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MealTemplateTools {

    private final IMealTemplateService mealTemplateService;

    @Tool(
            name = "get_meal_template",
            description = "Fetch a meal template by ID, including dish category counts, required dietary tags, and forbidden dietary tags"
    )
    public MealTemplateDto getMealTemplate(
            @ToolParam(description = "Meal template ID") String templateId) {
        return mealTemplateService.fetchMealTemplate(templateId);
    }

    @Tool(
            name = "list_meal_templates",
            description = "List all meal templates with their category requirements and dietary tag constraints"
    )
    public List<MealTemplateDto> listMealTemplates() {
        return mealTemplateService.fetchAllMealTemplates();
    }

    @Tool(
            name = "validate_meal_template_dishes",
            description = "Validate whether a dish selection matches a meal template. Checks dish count, category counts, required dietary tags, and forbidden dietary tags"
    )
    public boolean validateMealTemplateDishes(
            @ToolParam(description = "Meal template ID") String templateId,
            @ToolParam(description = "Selected dish IDs to validate") List<String> dishIds) {
        return mealTemplateService.validateDishSelection(templateId, dishIds);
    }
}
