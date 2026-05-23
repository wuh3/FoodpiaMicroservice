package com.foodopia.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {

    // Only last 4 digits stored
    @Field("last_four")
    private String lastFour;

    @Field("brand")
    private String brand;

    @Field("expiry_month")
    private int expiryMonth;

    @Field("expiry_year")
    private int expiryYear;

    @Field("cardholder_name")
    private String cardholderName;
}