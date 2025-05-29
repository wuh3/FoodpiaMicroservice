package com.foodopia.authentication.dto;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponse {
    @Getter
    private boolean valid;
    private String message;

    // User information (only populated if token is valid)
    private String username;
    private String userId;
    private AbstractFoodopiaUser.Role role;
    private List<String> authorities;

    // Account status information
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public boolean isInvalid() {
        return !valid;
    }

    public boolean hasUserInfo() {
        return username != null && userId != null;
    }

    public boolean isUserActive() {
        return enabled && accountNonExpired && accountNonLocked && credentialsNonExpired;
    }

    // Static factory methods for common responses
    public static ValidationResponse valid(String username, String userId, AbstractFoodopiaUser.Role role,
                                           List<String> authorities, boolean accountNonExpired,
                                           boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        return ValidationResponse.builder()
                .valid(true)
                .message("Token is valid")
                .username(username)
                .userId(userId)
                .role(role)
                .authorities(authorities)
                .accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked)
                .credentialsNonExpired(credentialsNonExpired)
                .enabled(enabled)
                .build();
    }

    public static ValidationResponse invalid(String message) {
        return ValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }

    public static ValidationResponse expired() {
        return ValidationResponse.builder()
                .valid(false)
                .message("Token has expired")
                .build();
    }

    public static ValidationResponse blacklisted() {
        return ValidationResponse.builder()
                .valid(false)
                .message("Token has been invalidated")
                .build();
    }

    public static ValidationResponse malformed() {
        return ValidationResponse.builder()
                .valid(false)
                .message("Token is malformed")
                .build();
    }

    public static ValidationResponse userNotFound() {
        return ValidationResponse.builder()
                .valid(false)
                .message("User not found")
                .build();
    }

    public static ValidationResponse accountInactive() {
        return ValidationResponse.builder()
                .valid(false)
                .message("User account is not active")
                .build();
    }
}