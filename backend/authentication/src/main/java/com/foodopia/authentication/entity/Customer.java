package com.foodopia.authentication.entity;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
@Getter @Setter @NoArgsConstructor
public class Customer extends AbstractFoodopiaUser {

    public Customer(String username, String email) {
        super();
        this.setUsername(username);
        this.setEmail(email);
        this.setRole(Role.CUSTOMER);
    }
}
