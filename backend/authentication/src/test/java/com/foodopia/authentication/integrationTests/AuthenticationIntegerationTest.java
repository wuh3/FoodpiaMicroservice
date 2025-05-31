package com.foodopia.authentication.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodopia.authentication.controller.AuthenticationController;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.entity.Customer;
import com.foodopia.authentication.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Testcontainers
@Transactional
class AuthenticationIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RequestUserDto validUserDto;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        customerRepository.deleteAll();

        validUserDto = new RequestUserDto();
        validUserDto.setUsername("integrationuser");
        validUserDto.setEmail("integration@example.com");
        validUserDto.setPassword("Password123");
        validUserDto.setConfirmPassword("Password123");
    }

    @Test
    @DisplayName("Complete user registration and authentication flow")
    void testCompleteUserFlow() throws Exception {
        // Step 1: Check username availability (should be available)
        mockMvc.perform(get("/auth/check-username/integrationuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.username").value("integrationuser"));

        // Step 2: Check email availability (should be available)
        mockMvc.perform(get("/auth/check-email/integration@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.email").value("integration@example.com"));

        // Step 3: Register new customer
        mockMvc.perform(post("/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer registered successfully"))
                .andExpect(jsonPath("$.username").value("integrationuser"));

        // Step 4: Verify user was created in database
        Optional<Customer> savedCustomer = customerRepository.findByUsername("integrationuser");
        assertThat(savedCustomer).isPresent();
        assertThat(savedCustomer.get().getEmail()).isEqualTo("integration@example.com");
        assertThat(savedCustomer.get().getRole()).isEqualTo(com.foodopia.authentication.domain.AbstractFoodopiaUser.Role.CUSTOMER);

        // Step 5: Check username availability again (should not be available now)
        mockMvc.perform(get("/auth/check-username/integrationuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.username").value("integrationuser"));

        // Step 6: Check email availability again (should not be available now)
        mockMvc.perform(get("/auth/check-email/integration@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.email").value("integration@example.com"));

        // Step 7: Attempt to register again with same username (should fail)
        mockMvc.perform(post("/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").containsString("Username already exists"));

        // Step 8: Login with correct credentials
        AuthenticationController.LoginRequest loginRequest = new AuthenticationController.LoginRequest();
        loginRequest.setUsername("integrationuser");
        loginRequest.setPassword("Password123");

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.userInfo.username").value("integrationuser"))
                .andExpect(jsonPath("$.userInfo.email").value("integration@example.com"))
                .andExpect(jsonPath("$.userInfo.role").value("CUSTOMER"))
                .andReturn().getResponse().getContentAsString();

        // Extract tokens from login response
        com.fasterxml.jackson.databind.JsonNode responseJson = objectMapper.readTree(loginResponse);
        String accessToken = responseJson.get("token").asText();
        String refreshToken = responseJson.get("refreshToken").asText();

        // Step 9: Validate the access token
        AuthenticationController.TokenValidationRequest tokenRequest =
                new AuthenticationController.TokenValidationRequest();
        tokenRequest.setToken(accessToken);

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Token is valid"))
                .andExpect(jsonPath("$.username").value("integrationuser"));

        // Step 10: Change password
        AuthenticationController.ChangePasswordRequest changePasswordRequest =
                new AuthenticationController.ChangePasswordRequest();
        changePasswordRequest.setToken(accessToken);
        changePasswordRequest.setCurrentPassword("Password123");
        changePasswordRequest.setNewPassword("NewPassword456");

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.message").value("Password changed successfully"));

        // Step 11: Verify old password no longer works
        AuthenticationController.LoginRequest oldPasswordLogin = new AuthenticationController.LoginRequest();
        oldPasswordLogin.setUsername("integrationuser");
        oldPasswordLogin.setPassword("Password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldPasswordLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        // Step 12: Verify new password works
        AuthenticationController.LoginRequest newPasswordLogin = new AuthenticationController.LoginRequest();
        newPasswordLogin.setUsername("integrationuser");
        newPasswordLogin.setPassword("NewPassword456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPasswordLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Step 13: Refresh token
        AuthenticationController.RefreshTokenRequest refreshRequest =
                new AuthenticationController.RefreshTokenRequest();
        refreshRequest.setRefreshToken(refreshToken);

        String refreshResponse = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn().getResponse().getContentAsString();

        // Extract new tokens
        com.fasterxml.jackson.databind.JsonNode refreshResponseJson = objectMapper.readTree(refreshResponse);
        String newAccessToken = refreshResponseJson.get("token").asText();

        // Step 14: Logout with new token
        AuthenticationController.LogoutRequest logoutRequest = new AuthenticationController.LogoutRequest();
        logoutRequest.setToken(newAccessToken);

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        // Step 15: Verify token is invalidated
        AuthenticationController.TokenValidationRequest invalidatedTokenRequest =
                new AuthenticationController.TokenValidationRequest();
        invalidatedTokenRequest.setToken(newAccessToken);

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidatedTokenRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void testLoginWithInvalidCredentials() throws Exception {
        // First register a user
        mockMvc.perform(post("/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isCreated());

        // Try to login with wrong password
        AuthenticationController.LoginRequest loginRequest = new AuthenticationController.LoginRequest();
        loginRequest.setUsername("integrationuser");
        loginRequest.setPassword("WrongPassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    @DisplayName("Should fail registration with invalid data")
    void testRegistrationWithInvalidData() throws Exception {
        // Test with mismatched passwords
        RequestUserDto invalidDto = new RequestUserDto();
        invalidDto.setUsername("testuser");
        invalidDto.setEmail("test@example.com");
        invalidDto.setPassword("Password123");
        invalidDto.setConfirmPassword("DifferentPassword");

        mockMvc.perform(post("/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpected(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Passwords do not match"));

        // Verify no user was created
        Optional<Customer> customer = customerRepository.findByUsername("testuser");
        assertThat(customer).isEmpty();
    }

    @Test
    @DisplayName("Should handle admin registration flow")
    void testAdminRegistrationFlow() throws Exception {
        // Register admin
        AuthenticationController.AdminRegistrationRequest adminRequest =
                new AuthenticationController.AdminRegistrationRequest();
        adminRequest.setUserDto(validUserDto);
        adminRequest.setAdminLevel("SUPER_ADMIN");

        mockMvc.perform(post("/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Administrator registered successfully"));

        // Login as admin
        AuthenticationController.LoginRequest loginRequest = new AuthenticationController.LoginRequest();
        loginRequest.setUsername("integrationuser");
        loginRequest.setPassword("Password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userInfo.role").value("ADMIN"))
                .andExpect(jsonPath("$.userInfo.adminLevel").value("SUPER_ADMIN"));
    }

    @Test
    @DisplayName("Should handle operator registration flow")
    void testOperatorRegistrationFlow() throws Exception {
        // Register operator
        AuthenticationController.OperatorRegistrationRequest operatorRequest =
                new AuthenticationController.OperatorRegistrationRequest();
        operatorRequest.setUserDto(validUserDto);
        operatorRequest.setDepartment("OPERATIONS");

        mockMvc.perform(post("/auth/register/operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operatorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operator registered successfully"));

        // Login as operator
        AuthenticationController.LoginRequest loginRequest = new AuthenticationController.LoginRequest();
        loginRequest.setUsername("integrationuser");
        loginRequest.setPassword("Password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userInfo.role").value("OPERATOR"))
                .andExpect(jsonPath("$.userInfo.department").value("OPERATIONS"));
    }

    @Test
    @DisplayName("Should handle kitchen user registration flow")
    void testKitchenUserRegistrationFlow() throws Exception {
        // Register kitchen user
        AuthenticationController.KitchenUserRegistrationRequest kitchenRequest =
                new AuthenticationController.KitchenUserRegistrationRequest();
        kitchenRequest.setUserDto(validUserDto);
        kitchenRequest.setStation("PREP_STATION_1");

        mockMvc.perform(post("/auth/register/kitchen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kitchenRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Kitchen User registered successfully"));

        // Login as kitchen user
        AuthenticationController.LoginRequest loginRequest = new AuthenticationController.LoginRequest();
        loginRequest.setUsername("integrationuser");
        loginRequest.setPassword("Password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.userInfo.role").value("KITCHEN"))
                .andExpect(jsonPath("$.userInfo.station").value("PREP_STATION_1"));
    }

    @Test
    @DisplayName("Should return health check status")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("authentication-service"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should handle validation errors properly")
    void testValidationErrors() throws Exception {
        // Test with invalid email format
        RequestUserDto invalidDto = new RequestUserDto();
        invalidDto.setUsername("u"); // Too short
        invalidDto.setEmail("invalid-email"); // Invalid format
        invalidDto.setPassword("weak"); // Too short
        invalidDto.setConfirmPassword("weak");

        mockMvc.perform(post("/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        // Verify no user was created
        Optional<Customer> customer = customerRepository.findByUsername("u");
        assertThat(customer).isEmpty();
    }
}