package com.foodopia.authentication.util;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:myDefaultSecretKeyThatShouldBeChangedInProduction123456789}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private Long refreshExpiration;

    // Generate secret key from string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract user ID from token
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    // Extract user role from token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract token type (access or refresh)
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract issued date from token
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    // Generic method to extract any claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid: {}", e.getMessage());
            throw e;
        }
    }

    // Check if token is expired
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // Check if token is a refresh token
    public Boolean isRefreshToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    // Check if token is an access token
    public Boolean isAccessToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "access".equals(tokenType) || tokenType == null; // null for backward compatibility
        } catch (Exception e) {
            return false;
        }
    }

    // Generate access token
    public String generateToken(AbstractFoodopiaUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole().name());
        claims.put("authorities", user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList());
        claims.put("tokenType", "access");

        return createToken(claims, user.getUsername(), expiration);
    }

    // Generate refresh token
    public String generateRefreshToken(AbstractFoodopiaUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("tokenType", "refresh");

        return createToken(claims, user.getUsername(), refreshExpiration);
    }

    // Create token with claims
    private String createToken(Map<String, Object> claims, String subject, Long validityPeriod) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityPeriod);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token against user details
    public Boolean validateToken(String token, AbstractFoodopiaUser user) {
        try {
            final String username = extractUsername(token);
            final String userId = extractUserId(token);

            return (username.equals(user.getUsername()) &&
                    userId.equals(user.getUserId()) &&
                    !isTokenExpired(token) &&
                    user.isEnabled() &&
                    user.isAccountNonExpired() &&
                    user.isAccountNonLocked() &&
                    user.isCredentialsNonExpired());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Validate token structure and signature (without user details)
    public Boolean validateTokenStructure(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token structure validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Get remaining validity time in milliseconds
    public Long getRemainingValidityTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            return Math.max(0, expiration.getTime() - now.getTime());
        } catch (Exception e) {
            return 0L;
        }
    }

    // Check if token will expire within specified minutes
    public Boolean willExpireWithin(String token, int minutes) {
        try {
            Date expiration = extractExpiration(token);
            Date threshold = new Date(System.currentTimeMillis() + (minutes * 60 * 1000));
            return expiration.before(threshold);
        } catch (Exception e) {
            return true;
        }
    }

    // Get token expiration time (for response)
    public Long getExpirationTime() {
        return expiration;
    }

    // Get refresh token expiration time
    public Long getRefreshExpirationTime() {
        return refreshExpiration;
    }

    // Create token from username (simple version)
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "access");
        return createToken(claims, username, expiration);
    }

    // Extract all user information from token
    public Map<String, Object> extractUserInfo(String token) {
        Claims claims = extractAllClaims(token);
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("username", claims.getSubject());
        userInfo.put("userId", claims.get("userId"));
        userInfo.put("role", claims.get("role"));
        userInfo.put("authorities", claims.get("authorities"));
        userInfo.put("tokenType", claims.get("tokenType"));
        userInfo.put("issuedAt", claims.getIssuedAt());
        userInfo.put("expiresAt", claims.getExpiration());

        return userInfo;
    }

    // Helper method to safely extract claims without throwing exceptions
    public <T> T safeExtractClaim(String token, Function<Claims, T> claimsResolver, T defaultValue) {
        try {
            return extractClaim(token, claimsResolver);
        } catch (Exception e) {
            log.warn("Failed to extract claim from token: {}", e.getMessage());
            return defaultValue;
        }
    }

    // Get token info for debugging (without sensitive data)
    public String getTokenInfo(String token) {
        try {
            String username = extractUsername(token);
            String userId = extractUserId(token);
            String role = extractRole(token);
            String tokenType = extractTokenType(token);
            Date issuedAt = extractIssuedAt(token);
            Date expiresAt = extractExpiration(token);

            return String.format("Token[user=%s, userId=%s, role=%s, type=%s, issued=%s, expires=%s]",
                    username, userId, role, tokenType, issuedAt, expiresAt);
        } catch (Exception e) {
            return "Invalid token: " + e.getMessage();
        }
    }
}