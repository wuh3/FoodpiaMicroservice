package com.foodopia.customer.mcp.tools;

import com.foodopia.customer.dto.UserProfileDto;
import com.foodopia.customer.service.IUserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserProfileTools {

    private final IUserProfileService userProfileService;

    @Tool(
            name = "get_user_profile",
            description = "Fetch a customer profile by user ID, including diet preferences and contact info"
    )
    public UserProfileDto getUserProfile(
            @ToolParam(description = "User ID") String userId) {
        return userProfileService.fetchProfileByUserId(userId);
    }
}
