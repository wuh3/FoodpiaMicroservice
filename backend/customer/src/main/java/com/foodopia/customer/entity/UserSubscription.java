package com.foodopia.customer.entity;

import com.foodopia.customer.entity.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document(collection = "user_subscriptions")
@CompoundIndexes({
        @CompoundIndex(name = "user_status_idx", def = "{'user_id': 1, 'status': 1}")
})
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription extends AuditableDocument {

    @Id
    private String id;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("plan_name")
    private String planName;

    @Field("plan_code")
    @Indexed
    private String planCode;

    @Field("plan_level")
    private int planLevel;

    @Field("meals_per_month")
    private int mealsPerMonth;

    @Field("status")
    @Indexed
    private SubscriptionStatus status;

    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;
}
