package com.foodopia.apiGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator foodopiaRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Authentication Service Routes
				.route(p -> p
						.path("/foodopia/auth/**")
						.filters(f -> f.rewritePath("/foodopia/auth/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://authentication-service"))

				// Meal Service Routes
				.route(p -> p
						.path("/foodopia/api/meals/**", "/foodopia/api/dishes/**")
						.filters(f -> f.rewritePath("/foodopia/api/(meals|dishes)/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://meal-service"))

				// Notification Service Routes
				.route(p -> p
						.path("/foodopia/api/notifications/**")
						.filters(f -> f.rewritePath("/foodopia/api/notifications/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://notification-service"))

				.build();
	}
}