package com.foodopia.notification.dto;

/**
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
) {}