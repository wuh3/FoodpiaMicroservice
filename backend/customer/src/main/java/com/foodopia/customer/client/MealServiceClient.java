package com.foodopia.customer.client;

import com.foodopia.customer.client.dto.MealPlanTypeClientDto;
import com.foodopia.customer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MealServiceClient {

    private static final Logger log = LoggerFactory.getLogger(MealServiceClient.class);

    private final RestClient mealServiceRestClient;

    public MealPlanTypeClientDto fetchMealPlanType(String planCode) {
        log.debug("Fetching meal plan type from meal-service with planCode: {}", planCode);
        try {
            return mealServiceRestClient.get()
                    .uri("/api/meal-plan-types/{planCode}", planCode)
                    .retrieve()
                    .body(MealPlanTypeClientDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("MealPlanType", "planCode", planCode);
        }
    }
}
