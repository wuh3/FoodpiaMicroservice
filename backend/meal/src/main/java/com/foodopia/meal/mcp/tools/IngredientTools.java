package com.foodopia.meal.mcp.tools;

import com.foodopia.meal.dto.IngredientDto;
import com.foodopia.meal.service.IIngredientService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class IngredientTools {

    private final IIngredientService ingredientService;

    @Tool(
            name = "get_ingredient",
            description = "Fetch a single ingredient by ID, including unit price and nutrition per 100g"
    )
    public IngredientDto getIngredient(
            @ToolParam(description = "Ingredient ID") String ingredientId) {
        return ingredientService.fetchIngredient(ingredientId);
    }

    @Tool(
            name = "get_ingredient_by_name",
            description = "Fetch a single ingredient by its unique name (case-insensitive)"
    )
    public IngredientDto getIngredientByName(
            @ToolParam(description = "Ingredient name") String name) {
        return ingredientService.fetchIngredientByName(name);
    }

    @Tool(
            name = "list_ingredients",
            description = "List all ingredients in the meal catalog"
    )
    public List<IngredientDto> listIngredients() {
        return ingredientService.fetchAllIngredients();
    }

    @Tool(
            name = "list_ingredients_by_category",
            description = "List ingredients filtered by category (e.g. protein, vegetable, grain)"
    )
    public List<IngredientDto> listIngredientsByCategory(
            @ToolParam(description = "Ingredient category") String category) {
        return ingredientService.fetchIngredientsByCategory(category);
    }
}
