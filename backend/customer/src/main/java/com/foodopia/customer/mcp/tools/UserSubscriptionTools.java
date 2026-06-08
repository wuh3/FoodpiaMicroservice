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
            description = "Fetch a single subscription by subscription ID"
    )
    public UserSubscriptionDto getSubscription(
            @ToolParam(description = "Subscription ID") String subscriptionId) {
        return userSubscriptionService.fetchSubscription(subscriptionId);
    }

    @Tool(
            name = "list_subscriptions_by_user_id",
            description = "List all subscriptions for a customer by user ID"
    )
    public List<UserSubscriptionDto> listSubscriptionsByUserId(
            @ToolParam(description = "User ID") String userId) {
        return userSubscriptionService.fetchSubscriptionsByUserId(userId);
    }
}
