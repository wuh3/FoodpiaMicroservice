package com.foodopia.authentication.entity;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "admins")
@Getter @Setter @NoArgsConstructor
public class Administrator extends AbstractFoodopiaUser {

    @Field("adminLevel")
    private String adminLevel;

    public Administrator(String username, String email, String adminLevel) {
        super();
        this.setUsername(username);
        this.setEmail(email);
        this.setRole(Role.ADMIN);
        this.adminLevel = adminLevel;
    }
}
