package com.foodopia.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAddressDto {

    private String id;

    @NotBlank(message = "userId cannot be null or empty")
    private String userId;

    @NotBlank(message = "label cannot be null or empty")
    private String label;

    private boolean isDefault;

    @NotBlank(message = "line1 cannot be null or empty")
    private String line1;

    private String line2;

    @NotBlank(message = "city cannot be null or empty")
    private String city;

    @NotBlank(message = "state cannot be null or empty")
    private String state;

    @NotBlank(message = "postalCode cannot be null or empty")
    private String postalCode;

    @NotBlank(message = "country cannot be null or empty")
    private String country;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
