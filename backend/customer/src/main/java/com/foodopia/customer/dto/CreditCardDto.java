package com.foodopia.customer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreditCardDto {

    @NotBlank
    @Pattern(regexp = "[0-9]{4}", message = "lastFour must be exactly 4 digits")
    private String lastFour;

    @NotBlank
    private String brand;

    @Min(1)
    @Max(12)
    private int expiryMonth;

    @Min(2025)
    private int expiryYear;

    @NotBlank
    private String cardholderName;
}
