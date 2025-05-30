package com.foodopia.authentication.controller;

import com.foodopia.authentication.dto.*;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.service.IAuthenticationService;
import com.foodopia.authentication.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AuthenticationController {

    private final IAuthenticationService authenticationService;
    private final IUserService IUserService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for username: {}", loginRequest.getUsername());

            JwtResponse response = authenticationService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            log.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for username: {}", loginRequest.getUsername(), e);

            JwtResponse errorResponse = JwtResponse.failure("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Register a new customer
     */
    @PostMapping("/register/customer")
    public ResponseEntity<Map<String, Object>> registerCustomer(
            @Valid @RequestBody RequestUserDto registrationDto,
            BindingResult bindingResult) {

        return handleRegistration(() -> {
            authenticationService.registerCustomer(registrationDto);
            log.info("Customer registered successfully: {}", registrationDto.getUsername());
        }, registrationDto, bindingResult, "Customer");
    }

    /**
     * Register a new administrator (admin-only endpoint)
     */
    @PostMapping("/register/admin")
    public ResponseEntity<Map<String, Object>> registerAdmin(
            @Valid @RequestBody AdminRegistrationRequest registrationRequest,
            BindingResult bindingResult) {

        return handleRegistration(() -> {
            authenticationService.registerAdmin(
                    registrationRequest.getUserDto(),
                    registrationRequest.getAdminLevel()
            );
            log.info("Administrator registered successfully: {}", registrationRequest.getUserDto().getUsername());
        }, registrationRequest.getUserDto(), bindingResult, "Administrator");
    }

    /**
     * Register a new operator (admin-only endpoint)
     */
    @PostMapping("/register/operator")
    public ResponseEntity<Map<String, Object>> registerOperator(
            @Valid @RequestBody OperatorRegistrationRequest registrationRequest,
            BindingResult bindingResult) {

        return handleRegistration(() -> {
            authenticationService.registerOperator(
                    registrationRequest.getUserDto(),
                    registrationRequest.getDepartment()
            );
            log.info("Operator registered successfully: {}", registrationRequest.getUserDto().getUsername());
        }, registrationRequest.getUserDto(), bindingResult, "Operator");
    }

    /**
     * Register a new kitchen user (admin-only endpoint)
     */
    @PostMapping("/register/kitchen")
    public ResponseEntity<Map<String, Object>> registerKitchenUser(
            @Valid @RequestBody KitchenUserRegistrationRequest registrationRequest,
            BindingResult bindingResult) {

        return handleRegistration(() -> {
            authenticationService.registerKitchenUser(
                    registrationRequest.getUserDto(),
                    registrationRequest.getStation()
            );
            log.info("Kitchen user registered successfully: {}", registrationRequest.getUserDto().getUsername());
        }, registrationRequest.getUserDto(), bindingResult, "Kitchen User");
    }

    /**
     * Validate JWT token
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateToken(@RequestBody TokenValidationRequest request) {
        try {
            ValidationResponse response = authenticationService.validateToken(request.getToken());

            if (response.isValid()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            log.error("Token validation failed", e);
            ValidationResponse errorResponse = ValidationResponse.invalid("Token validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Refresh JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            JwtResponse response = authenticationService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            JwtResponse errorResponse = JwtResponse.failure("Failed to refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Logout and invalidate token
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody LogoutRequest request) {
        try {
            authenticationService.invalidateToken(request.getToken());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logged out successfully");

            log.info("User logged out successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Logout failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Logout failed");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Change password
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authenticationService.changePassword(
                    request.getToken(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");

            log.info("Password changed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Password change failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Check username availability
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@PathVariable String username) {
        boolean available = IUserService.isUsernameAvailable(username);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("username", username);

        return ResponseEntity.ok(response);
    }

    /**
     * Check email availability
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@PathVariable String email) {
        boolean available = IUserService.isEmailAvailable(email);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("email", email);

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "authentication-service");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // Private helper method for handling registration
    private ResponseEntity<Map<String, Object>> handleRegistration(
            Runnable registrationAction,
            RequestUserDto dto,
            BindingResult bindingResult,
            String userType) {

        Map<String, Object> response = new HashMap<>();

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    )));
            return ResponseEntity.badRequest().body(response);
        }

        // Check password confirmation
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            response.put("success", false);
            response.put("message", "Passwords do not match");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            registrationAction.run();

            response.put("success", true);
            response.put("message", userType + " registered successfully");
            response.put("username", dto.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (UserAlreadyExistsException e) {
            log.warn("Registration failed - user already exists: {}", e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

        } catch (Exception e) {
            log.error("Registration failed for {}: {}", userType, e.getMessage(), e);

            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Request DTOs
    @lombok.Data
    public static class LoginRequest {
        @jakarta.validation.constraints.NotBlank(message = "Username is required")
        private String username;

        @jakarta.validation.constraints.NotBlank(message = "Password is required")
        private String password;
    }

    @lombok.Data
    public static class AdminRegistrationRequest {
        @Valid
        private RequestUserDto userDto;

        @jakarta.validation.constraints.NotBlank(message = "Admin level is required")
        private String adminLevel;
    }

    @lombok.Data
    public static class OperatorRegistrationRequest {
        @Valid
        private RequestUserDto userDto;

        @jakarta.validation.constraints.NotBlank(message = "Department is required")
        private String department;
    }

    @lombok.Data
    public static class KitchenUserRegistrationRequest {
        @Valid
        private RequestUserDto userDto;

        @jakarta.validation.constraints.NotBlank(message = "Station is required")
        private String station;
    }

    @lombok.Data
    public static class TokenValidationRequest {
        @jakarta.validation.constraints.NotBlank(message = "Token is required")
        private String token;
    }

    @lombok.Data
    public static class RefreshTokenRequest {
        @jakarta.validation.constraints.NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @lombok.Data
    public static class LogoutRequest {
        @jakarta.validation.constraints.NotBlank(message = "Token is required")
        private String token;
    }

    @lombok.Data
    public static class ChangePasswordRequest {
        @jakarta.validation.constraints.NotBlank(message = "Token is required")
        private String token;

        @jakarta.validation.constraints.NotBlank(message = "Current password is required")
        private String currentPassword;

        @jakarta.validation.constraints.NotBlank(message = "New password is required")
        @jakarta.validation.constraints.Size(min = 8, message = "New password must be at least 8 characters long")
        @jakarta.validation.constraints.Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "New password must contain lower case, upper case letters and a digit"
        )
        private String newPassword;
    }
}