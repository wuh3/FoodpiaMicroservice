package com.foodopia.authentication.service;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.dto.UserInfoResponse;
import com.foodopia.authentication.exception.UserAlreadyExistsException;

import java.util.Optional;

public interface UserService {
    // User lookup methods
    Optional<AbstractFoodopiaUser> findUserByUsername(String username);
    Optional<AbstractFoodopiaUser> findUserByEmail(String email);
    Optional<AbstractFoodopiaUser> findUserById(String userId);

    // User availability checks
    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);

    // User creation methods
    void createCustomer(RequestUserDto dto) throws UserAlreadyExistsException;
    void createAdmin(RequestUserDto dto, String adminLevel) throws UserAlreadyExistsException;
    void createOperator(RequestUserDto dto, String department) throws UserAlreadyExistsException;
    void createKitchenUser(RequestUserDto dto, String station) throws UserAlreadyExistsException;

    // User management
    void updateUserStatus(String userId, boolean enabled);
    void saveUser(AbstractFoodopiaUser user);
    UserInfoResponse buildUserInfoResponse(AbstractFoodopiaUser user);

    // Password management
    void updatePassword(String userId, String newPassword);
    boolean validatePassword(String rawPassword, String encodedPassword);
}