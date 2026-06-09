package com.foodopia.customer.client.dto;

import lombok.Data;

@Data
public class PlanLevelClientDto {

    private int level;
    private int mealsPerMonth;
    private double monthlyPrice;
}
