package com.foodopia.authentication.unitTests;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.*;
import com.foodopia.authentication.entity.Customer;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.service.IUserService;
import com.foodopia.authentication.service.impl.IAuthenticationServiceImpl;
import com.foodopia.authentication.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private IUserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private IAuthenticationServiceImpl authenticationService;

    private Customer testCustomer;
    private RequestUserDto testUserDto;
    private UserInfoResponse testUserInfo;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("testuser", "test@example.com");
        testCustomer.setUserId("user123");
        testCustomer.setPassword("$2a$12$encodedPassword");
        testCustomer.setEnabled(true);
        testCustomer.setAccountNonExpired(true);
        testCustomer.setAccountNonLocked(true);
        testCustomer.setCredentialsNonExpired(true);

        testUserDto = new RequestUserDto();
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPassword("Password123");
        testUserDto.setConfirmPassword("Password123");

        testUserInfo = UserInfoResponse.builder()
                .userId("user123")
                .username("testuser")
                .email("test@example.com")
                .role(AbstractFoodopiaUser.Role.CUSTOMER)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void testAuthenticate_Success() {
        // Given
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword("Password123", testCustomer.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(testCustomer)).thenReturn("jwt-token");
        when(jwtUtil.generateRefreshToken(testCustomer)).thenReturn("refresh-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);
        when(userService.buildUserInfoResponse(testCustomer)).thenReturn(testUserInfo);

        // When
        JwtResponse response = authenticationService.authenticate("testuser", "Password123");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Authentication successful");
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUserInfo()).isEqualTo(testUserInfo);

        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword("Password123", testCustomer.getPassword());
        verify(jwtUtil).generateToken(testCustomer);
        verify(jwtUtil).generateRefreshToken(testCustomer);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testAuthenticate_UserNotFound() {
        // Given
        when(userService.findUserByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate("nonexistent", "password"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(userService).findUserByUsername("nonexistent");
        verify(userService, never()).validatePassword(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception for invalid password")
    void testAuthenticate_InvalidPassword() {
        // Given
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword("wrongpassword", testCustomer.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate("testuser", "wrongpassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");

        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword("wrongpassword", testCustomer.getPassword());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw exception for disabled user account")
    void testAuthenticate_DisabledAccount() {
        // Given
        testCustomer.setEnabled(false);
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword("Password123", testCustomer.getPassword())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate("testuser", "Password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User account is disabled");

        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword("Password123", testCustomer.getPassword());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should register customer successfully")
    void testRegisterCustomer_Success() {
        // Given
        doNothing().when(userService).createCustomer(testUserDto);

        // When
        assertThatCode(() -> authenticationService.registerCustomer(testUserDto))
                .doesNotThrowAnyException();

        // Then
        verify(userService).createCustomer(testUserDto);
    }

    @Test
    @DisplayName("Should propagate exception when customer already exists")
    void testRegisterCustomer_UserAlreadyExists() {
        // Given
        doThrow(new UserAlreadyExistsException("Username already exists"))
                .when(userService).createCustomer(testUserDto);

        // When & Then
        assertThatThrownBy(() -> authenticationService.registerCustomer(testUserDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username already exists");

        verify(userService).createCustomer(testUserDto);
    }

    @Test
    @DisplayName("Should register admin successfully")
    void testRegisterAdmin_Success() {
        // Given
        doNothing().when(userService).createAdmin(testUserDto, "SUPER_ADMIN");

        // When
        assertThatCode(() -> authenticationService.registerAdmin(testUserDto, "SUPER_ADMIN"))
                .doesNotThrowAnyException();

        // Then
        verify(userService).createAdmin(testUserDto, "SUPER_ADMIN");
    }

    @Test
    @DisplayName("Should register operator successfully")
    void testRegisterOperator_Success() {
        // Given
        doNothing().when(userService).createOperator(testUserDto, "OPERATIONS");

        // When
        assertThatCode(() -> authenticationService.registerOperator(testUserDto, "OPERATIONS"))
                .doesNotThrowAnyException();

        // Then
        verify(userService).createOperator(testUserDto, "OPERATIONS");
    }

    @Test
    @DisplayName("Should register kitchen user successfully")
    void testRegisterKitchenUser_Success() {
        // Given
        doNothing().when(userService).createKitchenUser(testUserDto, "PREP_STATION_1");

        // When
        assertThatCode(() -> authenticationService.registerKitchenUser(testUserDto, "PREP_STATION_1"))
                .doesNotThrowAnyException();

        // Then
        verify(userService).createKitchenUser(testUserDto, "PREP_STATION_1");
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateToken_Success() {
        // Given
        String token = "valid-jwt-token";
        when(jwtUtil.isTokenExpired(token)).thenReturn(false);
        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(jwtUtil.validateToken(token, testCustomer)).thenReturn(true);

        // When
        ValidationResponse response = authenticationService.validateToken(token);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Token is valid");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getUserId()).isEqualTo("user123");
        assertThat(response.getRole()).isEqualTo(AbstractFoodopiaUser.Role.CUSTOMER);

        verify(jwtUtil).isTokenExpired(token);
        verify(jwtUtil).extractUsername(token);
        verify(userService).findUserByUsername("testuser");
        verify(jwtUtil).validateToken(token, testCustomer);
    }

    @Test
    @DisplayName("Should return invalid response for expired token")
    void testValidateToken_ExpiredToken() {
        // Given
        String expiredToken = "expired-jwt-token";
        when(jwtUtil.isTokenExpired(expiredToken)).thenReturn(true);

        // When
        ValidationResponse response = authenticationService.validateToken(expiredToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Token has expired");

        verify(jwtUtil).isTokenExpired(expiredToken);
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(userService, never()).findUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should return invalid response for blacklisted token")
    void testValidateToken_BlacklistedToken() {
        // Given
        String blacklistedToken = "blacklisted-token";
        authenticationService.invalidateToken(blacklistedToken); // Add to blacklist first

        // When
        ValidationResponse response = authenticationService.validateToken(blacklistedToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Token has been invalidated");

        verify(jwtUtil, never()).isTokenExpired(anyString());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshToken_Success() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("testuser");
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(jwtUtil.validateToken(refreshToken, testCustomer)).thenReturn(true);
        when(jwtUtil.generateToken(testCustomer)).thenReturn("new-jwt-token");
        when(jwtUtil.generateRefreshToken(testCustomer)).thenReturn("new-refresh-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);
        when(userService.buildUserInfoResponse(testCustomer)).thenReturn(testUserInfo);

        // When
        JwtResponse response = authenticationService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Token refreshed successfully");
        assertThat(response.getToken()).isEqualTo("new-jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

        verify(jwtUtil).isRefreshToken(refreshToken);
        verify(jwtUtil).extractUsername(refreshToken);
        verify(userService).findUserByUsername("testuser");
        verify(jwtUtil).validateToken(refreshToken, testCustomer);
        verify(jwtUtil).generateToken(testCustomer);
        verify(jwtUtil).generateRefreshToken(testCustomer);
    }

    @Test
    @DisplayName("Should throw exception for invalid refresh token")
    void testRefreshToken_InvalidToken() {
        // Given
        String invalidRefreshToken = "invalid-refresh-token";
        when(jwtUtil.isRefreshToken(invalidRefreshToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.refreshToken(invalidRefreshToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid refresh token");

        verify(jwtUtil).isRefreshToken(invalidRefreshToken);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("Should invalidate token successfully")
    void testInvalidateToken_Success() {
        // Given
        String token = "jwt-token-to-invalidate";

        // When
        assertThatCode(() -> authenticationService.invalidateToken(token))
                .doesNotThrowAnyException();

        // Then - Verify token is blacklisted by trying to validate it
        ValidationResponse response = authenticationService.validateToken(token);
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Token has been invalidated");
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() {
        // Given
        String token = "valid-jwt-token";
        String currentPassword = "OldPassword123";
        String newPassword = "NewPassword123";

        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword(currentPassword, testCustomer.getPassword())).thenReturn(true);
        doNothing().when(userService).updatePassword(testCustomer.getUserId(), newPassword);

        // When
        assertThatCode(() -> authenticationService.changePassword(token, currentPassword, newPassword))
                .doesNotThrowAnyException();

        // Then
        verify(jwtUtil).extractUsername(token);
        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword(currentPassword, testCustomer.getPassword());
        verify(userService).updatePassword(testCustomer.getUserId(), newPassword);
    }

    @Test
    @DisplayName("Should throw exception for incorrect current password")
    void testChangePassword_WrongCurrentPassword() {
        // Given
        String token = "valid-jwt-token";
        String wrongCurrentPassword = "WrongPassword";
        String newPassword = "NewPassword123";

        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword(wrongCurrentPassword, testCustomer.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.changePassword(token, wrongCurrentPassword, newPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Current password is incorrect");

        verify(jwtUtil).extractUsername(token);
        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword(wrongCurrentPassword, testCustomer.getPassword());
        verify(userService, never()).updatePassword(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception for locked user account")
    void testAuthenticate_LockedAccount() {
        // Given
        testCustomer.setAccountNonLocked(false);
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword("Password123", testCustomer.getPassword())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate("testuser", "Password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User account is locked");

        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword("Password123", testCustomer.getPassword());
    }

    @Test
    @DisplayName("Should throw exception for expired user account")
    void testAuthenticate_ExpiredAccount() {
        // Given
        testCustomer.setAccountNonExpired(false);
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword("Password123", testCustomer.getPassword())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate("testuser", "Password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User account has expired");

        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword("Password123", testCustomer.getPassword());
    }

    @Test
    @DisplayName("Should throw exception for expired credentials")
    void testAuthenticate_ExpiredCredentials() {
        // Given
        testCustomer.setCredentialsNonExpired(false);
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(userService.validatePassword("Password123", testCustomer.getPassword())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate("testuser", "Password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User credentials have expired");

        verify(userService).findUserByUsername("testuser");
        verify(userService).validatePassword("Password123", testCustomer.getPassword());
    }
}