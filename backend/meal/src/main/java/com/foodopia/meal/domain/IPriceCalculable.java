package com.foodopia.meal.domain;

public interface IPriceCalculable {
    double calculateCost();
    double calculatePrice(double markup);
}
