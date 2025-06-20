package com.foodopia.notification.functions;

import com.foodopia.notification.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class NotificationFunctions {

    private static final Logger log = LoggerFactory.getLogger(NotificationFunctions.class);

    @Bean
    public Function<AuthEventDto, AuthEventDto> authEmail() {
        return authEventDto -> {
            log.info("Processing authentication event: {} for user: {}",
                    authEventDto.eventType(), authEventDto.email());

            switch (authEventDto.eventType()) {
                case "USER_REGISTERED":
                    sendWelcomeEmail(authEventDto);
                    break;
                case "PASSWORD_CHANGED":
                    sendPasswordChangedEmail(authEventDto);
                    break;
                case "ACCOUNT_LOCKED":
                    sendAccountLockedEmail(authEventDto);
                    break;
                case "ACCOUNT_UNLOCKED":
                    sendAccountUnlockedEmail(authEventDto);
                    break;
                case "NEW_DEVICE_LOGIN":
                    sendNewDeviceLoginEmail(authEventDto);
                    break;
                case "MULTIPLE_FAILED_ATTEMPTS":
                    sendFailedAttemptsEmail(authEventDto);
                    break;
                default:
                    log.warn("Unknown event type: {}", authEventDto.eventType());
            }

            return authEventDto;
        };
    }

    @Bean
    public Function<AuthEventDto, String> authSms() {
        return authEventDto -> {
            log.info("Sending SMS for event: {} to user: {}",
                    authEventDto.eventType(), authEventDto.username());

            // In production, integrate with SMS service
            // For now, just log the action
            String message = createSmsMessage(authEventDto);
            log.info("SMS content: {}", message);

            return authEventDto.userId();
        };
    }

    private void sendWelcomeEmail(AuthEventDto event) {
        String subject = "Welcome to Foodopia!";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Welcome to Foodopia! Your account has been successfully created.\n\n" +
                        "Start exploring our delicious meal plans and enjoy healthy, home-cooked meals delivered to your door.\n\n" +
                        "Best regards,\n" +
                        "The Foodopia Team",
                event.username()
        );

        log.info("Sending welcome email to {}: Subject: {}", event.email(), subject);
        log.debug("Email body: {}", body);
        // TODO: Integrate with actual email service
    }

    private void sendPasswordChangedEmail(AuthEventDto event) {
        String subject = "Password Changed - Foodopia";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Your password has been successfully changed on %s.\n\n" +
                        "If you did not make this change, please contact our support team immediately.\n\n" +
                        "Best regards,\n" +
                        "The Foodopia Team",
                event.username(),
                event.timestamp()
        );

        log.info("Sending password changed email to {}: Subject: {}", event.email(), subject);
        log.debug("Email body: {}", body);
    }

    private void sendAccountLockedEmail(AuthEventDto event) {
        String subject = "Security Alert: Account Locked - Foodopia";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Your Foodopia account has been locked due to suspicious activity on %s.\n\n" +
                        "To unlock your account, please reset your password or contact support.\n\n" +
                        "Best regards,\n" +
                        "The Foodopia Security Team",
                event.username(),
                event.timestamp()
        );

        log.info("Sending account locked email to {}: Subject: {}", event.email(), subject);
        log.debug("Email body: {}", body);
    }

    private void sendAccountUnlockedEmail(AuthEventDto event) {
        String subject = "Account Unlocked - Foodopia";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Good news! Your Foodopia account has been successfully unlocked on %s.\n\n" +
                        "You can now log in with your credentials.\n\n" +
                        "Best regards,\n" +
                        "The Foodopia Team",
                event.username(),
                event.timestamp()
        );

        log.info("Sending account unlocked email to {}: Subject: {}", event.email(), subject);
        log.debug("Email body: {}", body);
    }

    private void sendNewDeviceLoginEmail(AuthEventDto event) {
        String subject = "New Device Login Detected - Foodopia";
        String body = String.format(
                "Dear %s,\n\n" +
                        "We detected a login to your Foodopia account from a new device:\n\n" +
                        "Time: %s\n" +
                        "Device: %s\n" +
                        "Location: %s\n" +
                        "IP Address: %s\n\n" +
                        "If this was you, no action is needed. Otherwise, please secure your account.\n\n" +
                        "Best regards,\n" +
                        "The Foodopia Security Team",
                event.username(),
                event.timestamp(),
                event.deviceInfo() != null ? event.deviceInfo() : "Unknown",
                event.location() != null ? event.location() : "Unknown",
                event.ipAddress() != null ? event.ipAddress() : "Unknown"
        );

        log.info("Sending new device login email to {}: Subject: {}", event.email(), subject);
        log.debug("Email body: {}", body);
    }

    private void sendFailedAttemptsEmail(AuthEventDto event) {
        String subject = "Security Alert: Multiple Failed Login Attempts - Foodopia";
        String body = String.format(
                "Dear %s,\n\n" +
                        "We detected %d failed login attempts on your Foodopia account.\n\n" +
                        "Last attempt: %s\n\n" +
                        "If these attempts were not made by you, please secure your account immediately.\n\n" +
                        "Best regards,\n" +
                        "The Foodopia Security Team",
                event.username(),
                event.failedAttempts() != null ? event.failedAttempts() : 3,
                event.timestamp()
        );

        log.info("Sending failed attempts email to {}: Subject: {}", event.email(), subject);
        log.debug("Email body: {}", body);
    }

    private String createSmsMessage(AuthEventDto event) {
        switch (event.eventType()) {
            case "USER_REGISTERED":
                return String.format("Welcome to Foodopia, %s! Your account is ready.", event.username());
            case "PASSWORD_CHANGED":
                return "Foodopia: Your password was changed. Not you? Call support.";
            case "ACCOUNT_LOCKED":
                return "Foodopia: Your account is locked. Reset password to unlock.";
            case "ACCOUNT_UNLOCKED":
                return "Foodopia: Your account is now unlocked.";
            case "NEW_DEVICE_LOGIN":
                return String.format("Foodopia: New login from %s. Not you? Secure your account.",
                        event.location() != null ? event.location() : "new location");
            case "MULTIPLE_FAILED_ATTEMPTS":
                return String.format("Foodopia: %d failed login attempts detected.",
                        event.failedAttempts() != null ? event.failedAttempts() : 3);
            default:
                return "Foodopia: Security notification";
        }
    }
}