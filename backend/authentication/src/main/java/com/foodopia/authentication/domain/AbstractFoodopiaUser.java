package com.foodopia.authentication.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AbstractFoodopiaUser implements UserDetails {

    @Id
    private String userId;

    @Field("username")
    @Indexed(unique = true)
    protected String username;

    @Field("email")
    @Indexed(unique = true)
    protected String email;

    @Field("role")
    protected Role role;

    @Field("password")
    @ToString.Exclude // Exclude password from toString for security
    protected String password;

    @Field("enabled")
    protected boolean enabled = true;

    @Field("accountNonExpired")
    protected boolean accountNonExpired = true;

    @Field("accountNonLocked")
    protected boolean accountNonLocked = true;

    @Field("credentialsNonExpired")
    protected boolean credentialsNonExpired = true;

    public enum Role {
        ADMIN,
        CUSTOMER,
        KITCHEN,
        OPERATOR,
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert role to Spring Security authority
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}