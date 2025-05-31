package com.foodopia.authentication.testData;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.dto.UserInfoResponse;
import com.foodopia.authentication.entity.*;

import java.util.Arrays;

/**
 * Factory class for creating test data objects
 */
public class TestDataFactory {

    public static RequestUserDto createValidUserDto() {
        RequestUserDto dto = new RequestUserDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("Password123");
        dto.setConfirmPassword("Password123");
        return dto;
    }

    public static RequestUserDto createValidUserDto(String username, String email) {
        RequestUserDto dto = new RequestUserDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword("Password123");
        dto.setConfirmPassword("Password123");
        return dto;
    }

    public static RequestUserDto createInvalidUserDto() {
        RequestUserDto dto = new RequestUserDto();
        dto.setUsername("u"); // Too short
        dto.setEmail("invalid-email"); // Invalid format
        dto.setPassword("weak"); // Too short and no uppercase/digit
        dto.setConfirmPassword("different"); // Doesn't match password
        return dto;
    }

    public static Customer createTestCustomer() {
        Customer customer = new Customer("testuser", "test@example.com");
        customer.setUserId("customer123");
        customer.setPassword("$2a$04$encodedPassword");
        customer.setEnabled(true);
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);
        return customer;
    }

    public static Administrator createTestAdmin() {
        Administrator admin = new Administrator("adminuser", "admin@example.com", "SUPER_ADMIN");
        admin.setUserId("admin123");
        admin.setPassword("$2a$04$encodedPassword");
        admin.setEnabled(true);
        admin.setAccountNonExpired(true);
        admin.setAccountNonLocked(true);
        admin.setCredentialsNonExpired(true);
        return admin;
    }

    public static Operator createTestOperator() {
        Operator operator = new Operator("operatoruser", "operator@example.com", "OPERATIONS");
        operator.setUserId("operator123");
        operator.setPassword("$2a$04$encodedPassword");
        operator.setPermissions(Arrays.asList("READ", "WRITE", "UPDATE"));
        operator.setEnabled(true);
        operator.setAccountNonExpired(true);
        operator.setAccountNonLocked(true);
        operator.setCredentialsNonExpired(true);
        return operator;
    }

    public static KitchenUser createTestKitchenUser() {
        KitchenUser kitchenUser = new KitchenUser("kitchenuser", "kitchen@example.com", "PREP_STATION_1");
        kitchenUser.setUserId("kitchen123");
        kitchenUser.setPassword("$2a$04$encodedPassword");
        kitchenUser.setEnabled(true);
        kitchenUser.setAccountNonExpired(true);
        kitchenUser.setAccountNonLocked(true);
        kitchenUser.setCredentialsNonExpired(true);
        return kitchenUser;
    }

    public static UserInfoResponse createTestUserInfoResponse() {
        return UserInfoResponse.builder()
                .userId("user123")
                .username("testuser")
                .email("test@example.com")
                .role(AbstractFoodopiaUser.Role.CUSTOMER)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    public static Customer createDisabledCustomer() {
        Customer customer = createTestCustomer();
        customer.setEnabled(false);
        return customer;
    }

    public static Customer createExpiredCustomer() {
        Customer customer = createTestCustomer();
        customer.setAccountNonExpired(false);
        return customer;
    }

    public static Customer createLockedCustomer() {
        Customer customer = createTestCustomer();
        customer.setAccountNonLocked(false);
        return customer;
    }

    public static Customer createCredentialsExpiredCustomer() {
        Customer customer = createTestCustomer();
        customer.setCredentialsNonExpired(false);
        return customer;
    }
}