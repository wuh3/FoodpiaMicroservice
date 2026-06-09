package com.foodopia.customer.mcp.tools;

import com.foodopia.customer.dto.UserSubscriptionDto;
import com.foodopia.customer.service.IUserSubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserSubscriptionTools {

    private final IUserSubscriptionService userSubscriptionService;

    @Tool(
            name = "get_subscription",
            description = "Fetch a single subscription by subscription ID, including planCode, planLevel, mealsPerMonth, planName, status, and dates"
    )
    public UserSubscriptionDto getSubscription(
            @ToolParam(description = "Subscription ID") String subscriptionId) {
        return userSubscriptionService.fetchSubscription(subscriptionId);
    }

    @Tool(
            name = "list_subscriptions_by_user_id",
            description = "List all subscriptions for a customer by user ID, including planCode, planLevel, and denormalized mealsPerMonth"
    )
    public List<UserSubscriptionDto> listSubscriptionsByUserId(
            @ToolParam(description = "User ID") String userId) {
        return userSubscriptionService.fetchSubscriptionsByUserId(userId);
    }

    @Tool(
            name = "list_active_subscriptions_by_user_id",
            description = "List active subscriptions for a customer by user ID. Use to resolve the user's current meal plan (planCode, planLevel, mealsPerMonth)"
    )
    public List<UserSubscriptionDto> listActiveSubscriptionsByUserId(
            @ToolParam(description = "User ID") String userId) {
        return userSubscriptionService.fetchActiveSubscriptionsByUserId(userId);
    }

    @Tool(
            name = "list_subscriptions_by_plan_code",
            description = "List all subscriptions for a meal plan type by planCode (e.g. low_fat_slim_body_plan). planCode references MealPlanType in meal-service"
    )
    public List<UserSubscriptionDto> listSubscriptionsByPlanCode(
            @ToolParam(description = "Meal plan code") String planCode) {
        return userSubscriptionService.fetchSubscriptionsByPlanCode(planCode);
    }
}
