package com.foodopia.authentication.service.impl;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.*;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.service.AuthenticationService;
import com.foodopia.authentication.service.UserService;
import com.foodopia.authentication.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // In-memory token blacklist (use Redis in production)
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public JwtResponse authenticate(String username, String password) {
        AbstractFoodopiaUser user = userService.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!userService.validatePassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        validateUserAccountStatus(user);

        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        log.info("User {} authenticated successfully", username);

        return JwtResponse.builder()
                .success(true)
                .message("Authentication successful")
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .userInfo(userService.buildUserInfoResponse(user))
                .build();
    }

    @Override
    public void registerCustomer(RequestUserDto dto) throws UserAlreadyExistsException {
        userService.createCustomer(dto);
    }

    @Override
    public void registerAdmin(RequestUserDto dto, String adminLevel) throws UserAlreadyExistsException {
        userService.createAdmin(dto, adminLevel);
    }

    @Override
    public void registerOperator(RequestUserDto dto, String department) throws UserAlreadyExistsException {
        userService.createOperator(dto, department);
    }

    @Override
    public void registerKitchenUser(RequestUserDto dto, String station) throws UserAlreadyExistsException {
        userService.createKitchenUser(dto, station);
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
            AbstractFoodopiaUser user = userService.findUserByUsername(username)
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
            AbstractFoodopiaUser user = userService.findUserByUsername(username)
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
                    .userInfo(userService.buildUserInfoResponse(user))
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
        AbstractFoodopiaUser user = userService.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!userService.validatePassword(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        userService.updatePassword(user.getUserId(), newPassword);
        log.info("Password changed for user: {}", username);
    }

    private void validateUserAccountStatus(AbstractFoodopiaUser user) {
        if (!user.isEnabled()) throw new RuntimeException("User account is disabled");
        if (!user.isAccountNonLocked()) throw new RuntimeException("User account is locked");
        if (!user.isAccountNonExpired()) throw new RuntimeException("User account has expired");
        if (!user.isCredentialsNonExpired()) throw new RuntimeException("User credentials have expired");
    }
}