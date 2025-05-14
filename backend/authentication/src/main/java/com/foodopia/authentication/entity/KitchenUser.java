package com.foodopia.authentication.entity;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "kitchen_users")
@Getter @Setter @NoArgsConstructor
public class KitchenUser extends AbstractFoodopiaUser {

    @Field("station")
    private String station;

    public KitchenUser(String username, String email, String station) {
        super();
        this.setUsername(username);
        this.setEmail(email);
        this.setRole(Role.KITCHEN);
        this.station = station;
    }
}
