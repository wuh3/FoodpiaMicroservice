package com.foodopia.meal.mcp.config;

import com.foodopia.meal.mcp.tools.DishTools;
import com.foodopia.meal.mcp.tools.IngredientTools;
import com.foodopia.meal.mcp.tools.MealPlanTypeTools;
import com.foodopia.meal.mcp.tools.MealTemplateTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider mealToolCallbackProvider(
            DishTools dishTools,
            IngredientTools ingredientTools,
            MealTemplateTools mealTemplateTools,
            MealPlanTypeTools mealPlanTypeTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dishTools, ingredientTools, mealTemplateTools, mealPlanTypeTools)
                .build();
    }
}
