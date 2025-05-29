package com.foodopia.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private boolean success;
    private String message;
    private String token;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;
    private UserInfoResponse userInfo;

    public boolean isSuccessful() {
        return success;
    }

    public String getFullToken() {
        return tokenType + " " + token;
    }

    // Static factory methods for common responses
    public static JwtResponse success(String token, String refreshToken, Long expiresIn, UserInfoResponse userInfo) {
        return JwtResponse.builder()
                .success(true)
                .message("Authentication successful")
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .userInfo(userInfo)
                .build();
    }

    public static JwtResponse failure(String message) {
        return JwtResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static JwtResponse refreshSuccess(String newToken, String newRefreshToken, Long expiresIn, UserInfoResponse userInfo) {
        return JwtResponse.builder()
                .success(true)
                .message("Token refreshed successfully")
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(expiresIn)
                .userInfo(userInfo)
                .build();
    }
}