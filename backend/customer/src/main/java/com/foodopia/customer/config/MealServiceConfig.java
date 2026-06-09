package com.foodopia.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MealServiceConfig {

    @Bean
    public RestClient mealServiceRestClient(
            @Value("${foodopia.meal-service.url:http://localhost:8082}") String mealServiceUrl) {
        return RestClient.builder()
                .baseUrl(mealServiceUrl)
                .build();
    }
}
