package com.foodopia.authentication.service.impl;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.*;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.service.IAuthenticationService;
import com.foodopia.authentication.service.IUserService;
import com.foodopia.authentication.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAuthenticationServiceImpl implements IAuthenticationService {

    private final IUserService IUserService;
    private final JwtUtil jwtUtil;
    private final StreamBridge streamBridge;

    // In-memory token blacklist (use Redis in production)
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public JwtResponse authenticate(String username, String password) {
        AbstractFoodopiaUser user = IUserService.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!IUserService.validatePassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        validateUserAccountStatus(user);

        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Send login notification with device/location info
        sendLoginNotification(user);

        log.info("User {} authenticated successfully", username);

        return JwtResponse.builder()
                .success(true)
                .message("Authentication successful")
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .userInfo(IUserService.buildUserInfoResponse(user))
                .build();
    }

    @Override
    public void registerCustomer(RequestUserDto dto) throws UserAlreadyExistsException {
        IUserService.createCustomer(dto);

        // Find the created user to get the userId
        AbstractFoodopiaUser user = IUserService.findUserByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created user"));

        // Send user registration notification
        sendRegistrationNotification(user);
    }

    @Override
    public void registerAdmin(RequestUserDto dto, String adminLevel) throws UserAlreadyExistsException {
        IUserService.createAdmin(dto, adminLevel);

        // Find the created user to get the userId
        AbstractFoodopiaUser user = IUserService.findUserByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created user"));

        // Send user registration notification
        sendRegistrationNotification(user);
    }

    @Override
    public void registerOperator(RequestUserDto dto, String department) throws UserAlreadyExistsException {
        IUserService.createOperator(dto, department);

        // Find the created user to get the userId
        AbstractFoodopiaUser user = IUserService.findUserByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created user"));

        // Send user registration notification
        sendRegistrationNotification(user);
    }

    @Override
    public void registerKitchenUser(RequestUserDto dto, String station) throws UserAlreadyExistsException {
        IUserService.createKitchenUser(dto, station);

        // Find the created user to get the userId
        AbstractFoodopiaUser user = IUserService.findUserByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created user"));

        // Send user registration notification
        sendRegistrationNotification(user);
    }

    @Override
    public ValidationResponse validateToken(String token) {
        try {
            if (blacklistedTokens.contains(token)) {
                return ValidationResponse.builder()
                        .valid(false)
                        .message("Token has been invalidated")
                        .build();
            }

            if (jwtUtil.isTokenExpired(token)) {
                return ValidationResponse.builder()
                        .valid(false)
                        .message("Token has expired")
                        .build();
            }

            String username = jwtUtil.extractUsername(token);
            AbstractFoodopiaUser user = IUserService.findUserByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (!jwtUtil.validateToken(token, user)) {
                return ValidationResponse.builder()
                        .valid(false)
                        .message("Invalid token")
                        .build();
            }

            return ValidationResponse.builder()
                    .valid(true)
                    .message("Token is valid")
                    .username(user.getUsername())
                    .userId(user.getUserId())
                    .role(user.getRole())
                    .authorities(user.getAuthorities().stream()
                            .map(auth -> auth.getAuthority())
                            .toList())
                    .accountNonExpired(user.isAccountNonExpired())
                    .accountNonLocked(user.isAccountNonLocked())
                    .credentialsNonExpired(user.isCredentialsNonExpired())
                    .enabled(user.isEnabled())
                    .build();

        } catch (Exception e) {
            log.error("Token validation failed", e);
            return ValidationResponse.builder()
                    .valid(false)
                    .message("Token validation failed")
                    .build();
        }
    }

    @Override
    public JwtResponse refreshToken(String token) {
        try {
            if (blacklistedTokens.contains(token) || !jwtUtil.isRefreshToken(token)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String username = jwtUtil.extractUsername(token);
            AbstractFoodopiaUser user = IUserService.findUserByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (!jwtUtil.validateToken(token, user)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String newToken = jwtUtil.generateToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            blacklistedTokens.add(token); // Invalidate old refresh token

            return JwtResponse.builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTime())
                    .userInfo(IUserService.buildUserInfoResponse(user))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh token: " + e.getMessage());
        }
    }

    @Override
    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
        log.info("Token invalidated");
    }

    @Override
    public void changePassword(String token, String currentPassword, String newPassword) {
        String username = jwtUtil.extractUsername(token);
        AbstractFoodopiaUser user = IUserService.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!IUserService.validatePassword(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        IUserService.updatePassword(user.getUserId(), newPassword);

        // Send password change notification
        sendPasswordChangeNotification(user);

        log.info("Password changed for user: {}", username);
    }

    private void validateUserAccountStatus(AbstractFoodopiaUser user) {
        if (!user.isEnabled()) throw new RuntimeException("User account is disabled");
        if (!user.isAccountNonLocked()) throw new RuntimeException("User account is locked");
        if (!user.isAccountNonExpired()) throw new RuntimeException("User account has expired");
        if (!user.isCredentialsNonExpired()) throw new RuntimeException("User credentials have expired");
    }

    /**
     * Send user registration notification
     */
    private void sendRegistrationNotification(AbstractFoodopiaUser user) {
        AuthEventDto authEvent = AuthEventDto.userRegistered(
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        );

        sendAuthEvent(authEvent);
        log.info("Registration notification sent for user: {}", user.getUsername());
    }

    /**
     * Send login notification with device/location info if available
     */
    private void sendLoginNotification(AbstractFoodopiaUser user) {
        String deviceInfo = getDeviceInfo();
        String location = getLocationInfo();
        String ipAddress = getClientIpAddress();

        AuthEventDto authEvent = AuthEventDto.newDeviceLogin(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                deviceInfo,
                location,
                ipAddress
        );

        sendAuthEvent(authEvent);
        log.info("Login notification sent for user: {}", user.getUsername());
    }

    /**
     * Send password change notification
     */
    private void sendPasswordChangeNotification(AbstractFoodopiaUser user) {
        AuthEventDto authEvent = AuthEventDto.passwordChanged(
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        );

        sendAuthEvent(authEvent);
        log.info("Password change notification sent for user: {}", user.getUsername());
    }

    /**
     * Send authentication event via Kafka to notification service
     * Following EazyBank StreamBridge pattern exactly
     */
    private void sendAuthEvent(AuthEventDto authEvent) {
        try {
            log.info("Sending authentication event: {} for user: {}",
                    authEvent.eventType(), authEvent.username());

            // Using the same pattern as EazyBank accounts service
            var result = streamBridge.send("sendAuthEvent-out-0", authEvent);

            if (result) {
                log.info("Authentication event successfully sent to Kafka for user: {}",
                        authEvent.username());
            } else {
                log.error("Failed to send authentication event to Kafka for user: {}",
                        authEvent.username());
            }
        } catch (Exception e) {
            log.error("Error sending authentication event for user: {} - {}",
                    authEvent.username(), e.getMessage(), e);
        }
    }

    // Helper methods to extract request context information
    private String getDeviceInfo() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.debug("Could not extract device info: {}", e.getMessage());
        }
        return null;
    }

    private String getLocationInfo() {
        // In production, this could use GeoIP service
        // For now, return null - notification service will handle gracefully
        return null;
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // Check for X-Forwarded-For header first (proxy/load balancer)
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }

                // Check for X-Real-IP header
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }

                // Fall back to remote address
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not extract client IP: {}", e.getMessage());
        }
        return null;
    }
}