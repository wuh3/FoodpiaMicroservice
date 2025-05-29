package com.foodopia.authentication.service;

import com.foodopia.authentication.dto.*;
import com.foodopia.authentication.exception.UserAlreadyExistsException;

public interface AuthenticationService {
    JwtResponse authenticate(String username, String password);
    void registerCustomer(RequestUserDto registrationDto) throws UserAlreadyExistsException;
    void registerAdmin(RequestUserDto registrationDto, String adminLevel) throws UserAlreadyExistsException;
    void registerOperator(RequestUserDto registrationDto, String department) throws UserAlreadyExistsException;
    void registerKitchenUser(RequestUserDto registrationDto, String station) throws UserAlreadyExistsException;
    ValidationResponse validateToken(String token);
    JwtResponse refreshToken(String token);
    void invalidateToken(String token);
    void changePassword(String token, String currentPassword, String newPassword);
}