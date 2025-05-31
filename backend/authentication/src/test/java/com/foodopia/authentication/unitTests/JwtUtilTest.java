package com.foodopia.authentication.unitTests;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.entity.Customer;
import com.foodopia.authentication.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Customer testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Set test properties using reflection
        ReflectionTestUtils.setField(jwtUtil, "secret", "myTestSecretKeyForJWTTokensThatIsLongEnoughForHmacSha256Algorithm");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L); // 24 hours
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800000L); // 7 days

        testUser = new Customer("testuser", "test@example.com");
        testUser.setUserId("user123");
        testUser.setPassword("encodedPassword");
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
    }

    @Test
    @DisplayName("Should generate valid access token")
    void testGenerateToken_Success() {
        // When
        String token = jwtUtil.generateToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts separated by dots

        // Verify token contents
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtil.extractUserId(token)).isEqualTo("user123");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("CUSTOMER");
        assertThat(jwtUtil.extractTokenType(token)).isEqualTo("access");
        assertThat(jwtUtil.isAccessToken(token)).isTrue();
        assertThat(jwtUtil.isRefreshToken(token)).isFalse();
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void testGenerateRefreshToken_Success() {
        // When
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3);

        // Verify token contents
        assertThat(jwtUtil.extractUsername(refreshToken)).isEqualTo("testuser");
        assertThat(jwtUtil.extractUserId(refreshToken)).isEqualTo("user123");
        assertThat(jwtUtil.extractTokenType(refreshToken)).isEqualTo("refresh");
        assertThat(jwtUtil.isRefreshToken(refreshToken)).isTrue();
        assertThat(jwtUtil.isAccessToken(refreshToken)).isFalse();
    }

    @Test
    @DisplayName("Should extract username from token")
    void testExtractUsername_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void testExtractUserId_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        String userId = jwtUtil.extractUserId(token);

        // Then
        assertThat(userId).isEqualTo("user123");
    }

    @Test
    @DisplayName("Should extract role from token")
    void testExtractRole_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        String role = jwtUtil.extractRole(token);

        // Then
        assertThat(role).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void testExtractExpiration_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Should extract issued date from token")
    void testExtractIssuedAt_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Date issuedAt = jwtUtil.extractIssuedAt(token);

        // Then
        assertThat(issuedAt).isNotNull();
        assertThat(issuedAt).isBeforeOrEqualTo(new Date());
    }

    @Test
    @DisplayName("Should validate token with correct user")
    void testValidateToken_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Boolean isValid = jwtUtil.validateToken(token, testUser);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token with wrong user")
    void testValidateToken_WrongUser() {
        // Given
        String token = jwtUtil.generateToken(testUser);
        Customer wrongUser = new Customer("wronguser", "wrong@example.com");
        wrongUser.setUserId("wrong123");
        wrongUser.setEnabled(true);
        wrongUser.setAccountNonExpired(true);
        wrongUser.setAccountNonLocked(true);
        wrongUser.setCredentialsNonExpired(true);

        // When
        Boolean isValid = jwtUtil.validateToken(token, wrongUser);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should invalidate token for disabled user")
    void testValidateToken_DisabledUser() {
        // Given
        String token = jwtUtil.generateToken(testUser);
        testUser.setEnabled(false);

        // When
        Boolean isValid = jwtUtil.validateToken(token, testUser);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should check if token is not expired")
    void testIsTokenExpired_NotExpired() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Should detect expired token")
    void testIsTokenExpired_Expired() {
        // Given - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1 millisecond
        String token = jwtUtil.generateToken(testUser);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        Boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Should validate token structure")
    void testValidateTokenStructure_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Boolean isValid = jwtUtil.validateTokenStructure(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate malformed token structure")
    void testValidateTokenStructure_Malformed() {
        // Given
        String malformedToken = "invalid.token.structure";

        // When
        Boolean isValid = jwtUtil.validateTokenStructure(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should get remaining validity time")
    void testGetRemainingValidityTime_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Long remainingTime = jwtUtil.getRemainingValidityTime(token);

        // Then
        assertThat(remainingTime).isPositive();
        assertThat(remainingTime).isLessThanOrEqualTo(86400000L); // Less than or equal to 24 hours
    }

    @Test
    @DisplayName("Should check if token will expire within specified time")
    void testWillExpireWithin_Success() {
        // Given
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60000L); // 1 minute
        String token = jwtUtil.generateToken(testUser);

        // When
        Boolean willExpire = jwtUtil.willExpireWithin(token, 2); // Within 2 minutes

        // Then
        assertThat(willExpire).isTrue();
    }

    @Test
    @DisplayName("Should not expire within specified time for long-lived token")
    void testWillExpireWithin_LongLived() {
        // Given
        String token = jwtUtil.generateToken(testUser); // 24 hours token

        // When
        Boolean willExpire = jwtUtil.willExpireWithin(token, 1); // Within 1 minute

        // Then
        assertThat(willExpire).isFalse();
    }

    @Test
    @DisplayName("Should extract all user information from token")
    void testExtractUserInfo_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        Map<String, Object> userInfo = jwtUtil.extractUserInfo(token);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.get("username")).isEqualTo("testuser");
        assertThat(userInfo.get("userId")).isEqualTo("user123");
        assertThat(userInfo.get("role")).isEqualTo("CUSTOMER");
        assertThat(userInfo.get("tokenType")).isEqualTo("access");
        assertThat(userInfo.get("issuedAt")).isNotNull();
        assertThat(userInfo.get("expiresAt")).isNotNull();
        assertThat(userInfo.get("authorities")).isNotNull();
    }

    @Test
    @DisplayName("Should generate token from username only")
    void testGenerateTokenFromUsername_Success() {
        // When
        String token = jwtUtil.generateToken("simpleuser");

        // Then
        assertThat(token).isNotNull();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("simpleuser");
        assertThat(jwtUtil.extractTokenType(token)).isEqualTo("access");
    }

    @Test
    @DisplayName("Should safely extract claims with default value")
    void testSafeExtractClaim_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        String username = jwtUtil.safeExtractClaim(token, claims -> claims.getSubject(), "default");
        String nonExistent = jwtUtil.safeExtractClaim(token, claims -> claims.get("nonexistent", String.class), "default");

        // Then
        assertThat(username).isEqualTo("testuser");
        assertThat(nonExistent).isEqualTo("default");
    }

    @Test
    @DisplayName("Should safely extract claims from invalid token")
    void testSafeExtractClaim_InvalidToken() {
        // Given
        String invalidToken = "invalid.token";

        // When
        String result = jwtUtil.safeExtractClaim(invalidToken, claims -> claims.getSubject(), "default");

        // Then
        assertThat(result).isEqualTo("default");
    }

    @Test
    @DisplayName("Should get token info for debugging")
    void testGetTokenInfo_Success() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // When
        String tokenInfo = jwtUtil.getTokenInfo(token);

        // Then
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo).contains("testuser");
        assertThat(tokenInfo).contains("user123");
        assertThat(tokenInfo).contains("CUSTOMER");
        assertThat(tokenInfo).contains("access");
    }

    @Test
    @DisplayName("Should get error info for invalid token")
    void testGetTokenInfo_InvalidToken() {
        // Given
        String invalidToken = "invalid.token";

        // When
        String tokenInfo = jwtUtil.getTokenInfo(invalidToken);

        // Then
        assertThat(tokenInfo).contains("Invalid token");
    }

    @Test
    @DisplayName("Should get expiration time")
    void testGetExpirationTime() {
        // When
        Long expirationTime = jwtUtil.getExpirationTime();

        // Then
        assertThat(expirationTime).isEqualTo(86400000L); // 24 hours
    }

    @Test
    @DisplayName("Should get refresh expiration time")
    void testGetRefreshExpirationTime() {
        // When
        Long refreshExpirationTime = jwtUtil.getRefreshExpirationTime();

        // Then
        assertThat(refreshExpirationTime).isEqualTo(604800000L); // 7 days
    }

    @Test
    @DisplayName("Should throw exception for completely invalid token")
    void testExtractClaim_InvalidToken() {
        // Given
        String invalidToken = "completely.invalid.token";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should handle null token gracefully")
    void testExtractClaim_NullToken() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should detect access token correctly")
    void testIsAccessToken_Success() {
        // Given
        String accessToken = jwtUtil.generateToken(testUser);
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        // When & Then
        assertThat(jwtUtil.isAccessToken(accessToken)).isTrue();
        assertThat(jwtUtil.isAccessToken(refreshToken)).isFalse();
    }

    @Test
    @DisplayName("Should detect refresh token correctly")
    void testIsRefreshToken_Success() {
        // Given
        String accessToken = jwtUtil.generateToken(testUser);
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        // When & Then
        assertThat(jwtUtil.isRefreshToken(refreshToken)).isTrue();
        assertThat(jwtUtil.isRefreshToken(accessToken)).isFalse();
    }

    @Test
    @DisplayName("Should handle token type detection for invalid tokens")
    void testTokenTypeDetection_InvalidToken() {
        // Given
        String invalidToken = "invalid.token";

        // When & Then
        assertThat(jwtUtil.isAccessToken(invalidToken)).isFalse();
        assertThat(jwtUtil.isRefreshToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("Should return zero remaining time for expired token")
    void testGetRemainingValidityTime_ExpiredToken() {
        // Given
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1 millisecond
        String token = jwtUtil.generateToken(testUser);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        Long remainingTime = jwtUtil.getRemainingValidityTime(token);

        // Then
        assertThat(remainingTime).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should return zero remaining time for invalid token")
    void testGetRemainingValidityTime_InvalidToken() {
        // Given
        String invalidToken = "invalid.token";

        // When
        Long remainingTime = jwtUtil.getRemainingValidityTime(invalidToken);

        // Then
        assertThat(remainingTime).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle will expire within for invalid token")
    void testWillExpireWithin_InvalidToken() {
        // Given
        String invalidToken = "invalid.token";

        // When
        Boolean willExpire = jwtUtil.willExpireWithin(invalidToken, 5);

        // Then
        assertThat(willExpire).isTrue(); // Should assume it will expire for safety
    }

    @Test
    @DisplayName("Should validate token with all account status checks")
    void testValidateToken_AccountStatusChecks() {
        // Given
        String token = jwtUtil.generateToken(testUser);

        // Test with expired account
        testUser.setAccountNonExpired(false);
        assertThat(jwtUtil.validateToken(token, testUser)).isFalse();

        // Reset and test with locked account
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(false);
        assertThat(jwtUtil.validateToken(token, testUser)).isFalse();

        // Reset and test with expired credentials
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(false);
        assertThat(jwtUtil.validateToken(token, testUser)).isFalse();

        // Reset and test with disabled account
        testUser.setCredentialsNonExpired(true);
        testUser.setEnabled(false);
        assertThat(jwtUtil.validateToken(token, testUser)).isFalse();

        // Reset all and should be valid
        testUser.setEnabled(true);
        assertThat(jwtUtil.validateToken(token, testUser)).isTrue();
    }
}