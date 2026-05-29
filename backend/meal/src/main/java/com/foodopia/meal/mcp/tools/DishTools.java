package com.foodopia.meal.mcp.tools;

import com.foodopia.meal.dto.DishDto;
import com.foodopia.meal.service.IDishService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DishTools {

    private final IDishService dishService;

    @Tool(
            name = "get_dish",
            description = "Fetch a single dish by ID, including ingredients, nutrition, and dietary tags"
    )
    public DishDto getDish(
            @ToolParam(description = "Dish ID") String dishId) {
        return dishService.fetchDish(dishId);
    }

    @Tool(
            name = "list_dishes",
            description = "List all dishes in the meal catalog"
    )
    public List<DishDto> listDishes() {
        return dishService.fetchAllDishes();
    }

    @Tool(
            name = "list_dishes_by_category",
            description = "List dishes filtered by category (e.g. main, side, dessert)"
    )
    public List<DishDto> listDishesByCategory(
            @ToolParam(description = "Dish category") String category) {
        return dishService.fetchDishesByCategory(category);
    }

    @Tool(
            name = "list_dishes_by_dietary_tag",
            description = "List dishes that include a dietary tag (case-insensitive, e.g. vegan, halal, gluten-free)"
    )
    public List<DishDto> listDishesByDietaryTag(
            @ToolParam(description = "Dietary tag to match") String dietaryTag) {
        return dishService.fetchDishesByDietaryTag(dietaryTag);
    }

    @Tool(
            name = "list_dishes_by_popularity",
            description = "List dishes with popularity score at or above the minimum (higher = more popular)"
    )
    public List<DishDto> listDishesByPopularity(
            @ToolParam(description = "Minimum popularity score (inclusive)") double minPopularityScore) {
        return dishService.fetchDishesByMinPopularityScore(minPopularityScore);
    }

    @Tool(
            name = "list_dishes_by_ingredient_id",
            description = "List dishes that include an ingredient, by ingredient ID"
    )
    public List<DishDto> listDishesByIngredientId(
            @ToolParam(description = "Ingredient ID") String ingredientId) {
        return dishService.fetchDishesByIngredientId(ingredientId);
    }

    @Tool(
            name = "list_dishes_by_ingredient_name",
            description = "List dishes that include an ingredient, by unique ingredient name (case-insensitive)"
    )
    public List<DishDto> listDishesByIngredientName(
            @ToolParam(description = "Ingredient name") String ingredientName) {
        return dishService.fetchDishesByIngredientName(ingredientName);
    }
}
