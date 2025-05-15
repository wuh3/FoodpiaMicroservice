package com.foodopia.authentication.mapper;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.entity.Administrator;
import com.foodopia.authentication.entity.Customer;
import com.foodopia.authentication.entity.KitchenUser;
import com.foodopia.authentication.entity.Operator;

public class UserMapper {
    public static Customer mapToCustomer(RequestUserDto dto, Customer user) {
        user.setUsername(dto.getUsername());
        user.setRole(AbstractFoodopiaUser.Role.CUSTOMER);
        return user;
    }

    public static Operator mapToOperator(RequestUserDto dto, Operator user) {
        user.setUsername(dto.getUsername());
        user.setRole(AbstractFoodopiaUser.Role.OPERATOR);
        return user;
    }

    public static Administrator mapToAdmin(RequestUserDto dto, Administrator user) {
        user.setUsername(dto.getUsername());
        user.setRole(AbstractFoodopiaUser.Role.ADMIN);
        return user;
    }

    public static KitchenUser mapToKitchen(RequestUserDto dto, KitchenUser user) {
        user.setUsername(dto.getUsername());
        user.setRole(AbstractFoodopiaUser.Role.KITCHEN);
        return user;
    }
}
