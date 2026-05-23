package com.foodopia.customer.dto;

import com.foodopia.customer.entity.enums.PaymentMethodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentMethodDto {

    private String id;

    @NotBlank(message = "userId cannot be null or empty")
    private String userId;

    @NotNull(message = "type cannot be null")
    private PaymentMethodType type;

    private boolean isDefault;

    @PositiveOrZero(message = "creditsBalance must be zero or greater")
    private double creditsBalance;

    @Valid
    private CreditCardDto creditCard;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @AssertTrue(message = "creditCard is required when type is CREDIT_CARD")
    public boolean isCreditCardValid() {
        return type != PaymentMethodType.CREDIT_CARD || creditCard != null;
    }

    @AssertTrue(message = "creditCard must be absent when type is CREDITS")
    public boolean isCreditsTypeValid() {
        return type != PaymentMethodType.CREDITS || creditCard == null;
    }
}
