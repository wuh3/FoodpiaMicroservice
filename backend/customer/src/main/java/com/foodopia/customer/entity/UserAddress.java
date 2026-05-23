package com.foodopia.customer.entity;

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

@Document(collection = "user_addresses")
@CompoundIndexes({
        @CompoundIndex(name = "user_default_idx", def = "{'user_id': 1, 'is_default': 1}")
})
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress extends AuditableDocument {

    @Id
    private String id;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("label")
    private String label;

    @Field("is_default")
    private boolean isDefault;

    @Field("line1")
    private String line1;

    @Field("line2")
    private String line2;

    @Field("city")
    private String city;

    @Field("state")
    private String state;

    @Field("postal_code")
    private String postalCode;

    @Field("country")
    private String country;
}
