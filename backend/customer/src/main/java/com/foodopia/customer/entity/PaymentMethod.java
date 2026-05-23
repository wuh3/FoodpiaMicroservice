package com.foodopia.customer.entity;

import com.foodopia.customer.entity.enums.PaymentMethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "payment_methods")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod extends AuditableDocument {

    @Id
    private String id;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("type")
    private PaymentMethodType type;

    @Field("is_default")
    private boolean isDefault;

    @Field("credits_balance")
    private double creditsBalance;

    @Field("credit_card")
    private CreditCard creditCard;
}
