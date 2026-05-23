package com.foodopia.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_profiles")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends AuditableDocument {

    @Id
    private String id;

    // Cross-service reference to authentication-service user (email/username live in auth)
    @Field("user_id")
    @Indexed(unique = true)
    private String userId;

    @Field("profile_pic_url")
    private String profilePicUrl;

    @Field("legal_name")
    private String legalName;

    @Field("nickname")
    private String nickname;

    @Field("phone")
    private String phone;

    @Field("diet_preference")
    private DietPreference dietPreference;
}
