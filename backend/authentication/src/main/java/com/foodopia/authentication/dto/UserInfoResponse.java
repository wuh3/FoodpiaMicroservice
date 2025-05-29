package com.foodopia.authentication.dto;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true) // Enable toBuilder() method
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Only include non-null fields in JSON
public class UserInfoResponse {

    // Basic user information
    private String userId;
    private String username;
    private String email;
    private AbstractFoodopiaUser.Role role;

    // Account status information
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    // Timestamps (if you add these fields to your entities later)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    // Role-specific fields (only populated based on user role)
    private String adminLevel;        // For Administrator
    private String station;           // For KitchenUser
    private String department;        // For Operator
    private List<String> permissions; // For Operator

    // Additional customer-specific fields (if needed later)
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String alias;

    // Convenience methods
    public boolean isActive() {
        return enabled && accountNonExpired && accountNonLocked && credentialsNonExpired;
    }

    public boolean isAdmin() {
        return role == AbstractFoodopiaUser.Role.ADMIN;
    }

    public boolean isCustomer() {
        return role == AbstractFoodopiaUser.Role.CUSTOMER;
    }

    public boolean isOperator() {
        return role == AbstractFoodopiaUser.Role.OPERATOR;
    }

    public boolean isKitchenUser() {
        return role == AbstractFoodopiaUser.Role.KITCHEN;
    }

    public String getRoleDisplayName() {
        if (role == null) return "Unknown";

        return switch (role) {
            case ADMIN -> "Administrator";
            case CUSTOMER -> "Customer";
            case OPERATOR -> "Operator";
            case KITCHEN -> "Kitchen Staff";
        };
    }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else if (alias != null) {
            return alias;
        } else {
            return username;
        }
    }

    public boolean hasRoleSpecificInfo() {
        return adminLevel != null || station != null || department != null ||
                (permissions != null && !permissions.isEmpty());
    }

    // Static factory methods for different user types
    public static UserInfoResponse fromCustomer(String userId, String username, String email,
                                                boolean enabled, boolean accountNonExpired,
                                                boolean accountNonLocked, boolean credentialsNonExpired) {
        return UserInfoResponse.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .role(AbstractFoodopiaUser.Role.CUSTOMER)
                .enabled(enabled)
                .accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked)
                .credentialsNonExpired(credentialsNonExpired)
                .build();
    }

    public static UserInfoResponse fromAdmin(String userId, String username, String email,
                                             String adminLevel, boolean enabled, boolean accountNonExpired,
                                             boolean accountNonLocked, boolean credentialsNonExpired) {
        return UserInfoResponse.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .role(AbstractFoodopiaUser.Role.ADMIN)
                .adminLevel(adminLevel)
                .enabled(enabled)
                .accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked)
                .credentialsNonExpired(credentialsNonExpired)
                .build();
    }

    public static UserInfoResponse fromOperator(String userId, String username, String email,
                                                String department, List<String> permissions,
                                                boolean enabled, boolean accountNonExpired,
                                                boolean accountNonLocked, boolean credentialsNonExpired) {
        return UserInfoResponse.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .role(AbstractFoodopiaUser.Role.OPERATOR)
                .department(department)
                .permissions(permissions)
                .enabled(enabled)
                .accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked)
                .credentialsNonExpired(credentialsNonExpired)
                .build();
    }

    public static UserInfoResponse fromKitchenUser(String userId, String username, String email,
                                                   String station, boolean enabled, boolean accountNonExpired,
                                                   boolean accountNonLocked, boolean credentialsNonExpired) {
        return UserInfoResponse.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .role(AbstractFoodopiaUser.Role.KITCHEN)
                .station(station)
                .enabled(enabled)
                .accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked)
                .credentialsNonExpired(credentialsNonExpired)
                .build();
    }

    // Builder method for minimal user info (useful for token validation responses)
    public static UserInfoResponse minimal(String userId, String username, AbstractFoodopiaUser.Role role) {
        return UserInfoResponse.builder()
                .userId(userId)
                .username(username)
                .role(role)
                .build();
    }

    // Copy constructor for updating existing user info
    public UserInfoResponse withUpdatedTimestamp() {
        return this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public UserInfoResponse withLastLogin() {
        return this.toBuilder()
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    // Sanitized version (removes sensitive information for public APIs)
    public UserInfoResponse sanitized() {
        return UserInfoResponse.builder()
                .userId(userId)
                .username(username)
                .role(role)
                .enabled(enabled)
                // Remove the roleDisplayName line since it's not a field
                .build();
    }

    // For admin views (includes all information)
    public UserInfoResponse detailed() {
        return this; // Return full object for admin users
    }
}