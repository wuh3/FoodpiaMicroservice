package com.foodopia.customer.mcp.config;

import com.foodopia.customer.mcp.tools.UserProfileTools;
import com.foodopia.customer.mcp.tools.UserSubscriptionTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider customerToolCallbackProvider(
            UserProfileTools userProfileTools,
            UserSubscriptionTools userSubscriptionTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(userProfileTools, userSubscriptionTools)
                .build();
    }
}
