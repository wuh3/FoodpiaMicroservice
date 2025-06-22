package com.foodopia.authentication.functions;

import com.foodopia.authentication.service.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFunctions {

    private final IAuthenticationService authenticationService;

    /**
     * Consumer function to handle notification confirmations from notification service
     * Input: userId from notification service after successful notification delivery
     */
    @Bean
    public Consumer<String> updateNotificationStatus() {
        return userId -> {
            log.info("Updating notification status for user ID: {}", userId);

            try {
                // Update notification delivery status in database if needed
                // For now, just log the successful delivery confirmation
                log.info("Notification delivery confirmed for user: {}", userId);

                // Future enhancement: Update user record with last notification timestamp
                // authenticationService.updateLastNotificationTime(userId);

            } catch (Exception e) {
                log.error("Failed to update notification status for user: {} - {}", userId, e.getMessage(), e);
            }
        };
    }

    /**
     * Additional consumer for handling notification failures (optional)
     * This could be used for retry logic or alerting
     */
    @Bean
    public Consumer<String> handleNotificationFailure() {
        return userId -> {
            log.warn("Notification delivery failed for user ID: {}", userId);

            try {
                // Handle notification failure - could trigger retry logic
                // authenticationService.retryNotification(userId);

            } catch (Exception e) {
                log.error("Failed to handle notification failure for user: {} - {}", userId, e.getMessage(), e);
            }
        };
    }
}