package com.foodopia.meal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public class DishIngredientDto {

    @NotEmpty(message = "Ingredient id cannot be null or empty")
    private String ingredientId;

    /**
     * Optional: populated in responses for convenience.
     * Requests should prefer sending only ingredientId + quantity.
     */
    private IngredientDto ingredient;

    @Positive(message = "Quantity must be greater than zero")
    private double quantity;

    private double cost;

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public IngredientDto getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientDto ingredient) {
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}