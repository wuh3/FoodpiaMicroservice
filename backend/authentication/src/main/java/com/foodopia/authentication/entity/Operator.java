package com.foodopia.authentication.entity;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "operators")
@Getter @Setter @NoArgsConstructor
public class Operator extends AbstractFoodopiaUser {

    @Field("department")
    private String department;

    @Field("permissions")
    private List<String> permissions;

    public Operator(String username, String email, String department) {
        super();
        this.setUsername(username);
        this.setEmail(email);
        this.setRole(Role.OPERATOR);
        this.department = department;
    }
}
