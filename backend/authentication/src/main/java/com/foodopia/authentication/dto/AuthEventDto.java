package com.foodopia.authentication.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO for authentication events sent to notification service via Kafka
 *
 * @param eventType Type of authentication event
 * @param userId User ID
 * @param username Username
 * @param email Email address
 * @param timestamp Event timestamp
 * @param deviceInfo Device information
 * @param location Location
 * @param ipAddress IP address
 * @param failedAttempts Number of failed attempts
 */
public record AuthEventDto(
        String eventType,
        String userId,
        String username,
        String email,
        String timestamp,
        String deviceInfo,
        String location,
        String ipAddress,
        Integer failedAttempts
) {

    public static final String USER_REGISTERED = "USER_REGISTERED";
    public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
    public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    public static final String ACCOUNT_UNLOCKED = "ACCOUNT_UNLOCKED";
    public static final String NEW_DEVICE_LOGIN = "NEW_DEVICE_LOGIN";
    public static final String MULTIPLE_FAILED_ATTEMPTS = "MULTIPLE_FAILED_ATTEMPTS";

    // Static factory methods for common events
    public static AuthEventDto userRegistered(String userId, String username, String email) {
        return new AuthEventDto(
                USER_REGISTERED,
                userId,
                username,
                email,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                null,
                null,
                null,
                null
        );
    }

    public static AuthEventDto passwordChanged(String userId, String username, String email) {
        return new AuthEventDto(
                PASSWORD_CHANGED,
                userId,
                username,
                email,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                null,
                null,
                null,
                null
        );
    }

    public static AuthEventDto accountLocked(String userId, String username, String email, Integer failedAttempts) {
        return new AuthEventDto(
                ACCOUNT_LOCKED,
                userId,
                username,
                email,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                null,
                null,
                null,
                failedAttempts
        );
    }

    public static AuthEventDto accountUnlocked(String userId, String username, String email) {
        return new AuthEventDto(
                ACCOUNT_UNLOCKED,
                userId,
                username,
                email,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                null,
                null,
                null,
                null
        );
    }

    public static AuthEventDto newDeviceLogin(String userId, String username, String email,
                                              String deviceInfo, String location, String ipAddress) {
        return new AuthEventDto(
                NEW_DEVICE_LOGIN,
                userId,
                username,
                email,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                deviceInfo,
                location,
                ipAddress,
                null
        );
    }

    public static AuthEventDto multipleFailedAttempts(String userId, String username, String email,
                                                      Integer failedAttempts, String ipAddress) {
        return new AuthEventDto(
                MULTIPLE_FAILED_ATTEMPTS,
                userId,
                username,
                email,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                null,
                null,
                ipAddress,
                failedAttempts
        );
    }
}