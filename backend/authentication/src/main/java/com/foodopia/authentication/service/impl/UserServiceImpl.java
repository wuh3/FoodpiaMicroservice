package com.foodopia.authentication.service.impl;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.dto.UserInfoResponse;
import com.foodopia.authentication.entity.*;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.repository.*;
import com.foodopia.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final CustomerRepository customerRepository;
    private final AdministratorRepository administratorRepository;
    private final OperatorRepository operatorRepository;
    private final KitchenUserRepository kitchenUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<AbstractFoodopiaUser> findUserByUsername(String username) {
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) return Optional.of(customer.get());

        Optional<Administrator> admin = administratorRepository.findByUsername(username);
        if (admin.isPresent()) return Optional.of(admin.get());

        Optional<Operator> operator = operatorRepository.findByUsername(username);
        if (operator.isPresent()) return Optional.of(operator.get());

        Optional<KitchenUser> kitchenUser = kitchenUserRepository.findByUsername(username);
        if (kitchenUser.isPresent()) return Optional.of(kitchenUser.get());

        return Optional.empty();
    }

    @Override
    public Optional<AbstractFoodopiaUser> findUserByEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) return Optional.of(customer.get());

        Optional<Administrator> admin = administratorRepository.findByEmail(email);
        if (admin.isPresent()) return Optional.of(admin.get());

        Optional<Operator> operator = operatorRepository.findByEmail(email);
        if (operator.isPresent()) return Optional.of(operator.get());

        Optional<KitchenUser> kitchenUser = kitchenUserRepository.findByEmail(email);
        if (kitchenUser.isPresent()) return Optional.of(kitchenUser.get());

        return Optional.empty();
    }

    @Override
    public Optional<AbstractFoodopiaUser> findUserById(String userId) {
        Optional<Customer> customer = customerRepository.findById(userId);
        if (customer.isPresent()) return Optional.of(customer.get());

        Optional<Administrator> admin = administratorRepository.findById(userId);
        if (admin.isPresent()) return Optional.of(admin.get());

        Optional<Operator> operator = operatorRepository.findById(userId);
        if (operator.isPresent()) return Optional.of(operator.get());

        Optional<KitchenUser> kitchenUser = kitchenUserRepository.findById(userId);
        if (kitchenUser.isPresent()) return Optional.of(kitchenUser.get());

        return Optional.empty();
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return findUserByUsername(username).isEmpty();
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return findUserByEmail(email).isEmpty();
    }

    @Override
    public void createCustomer(RequestUserDto dto) throws UserAlreadyExistsException {
        validateUserRegistration(dto);
        Customer customer = new Customer(dto.getUsername(), dto.getEmail());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customerRepository.save(customer);
        log.info("Customer created: {}", dto.getUsername());
    }

    @Override
    public void createAdmin(RequestUserDto dto, String adminLevel) throws UserAlreadyExistsException {
        validateUserRegistration(dto);
        Administrator admin = new Administrator(dto.getUsername(), dto.getEmail(), adminLevel);
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        administratorRepository.save(admin);
        log.info("Administrator created: {}", dto.getUsername());
    }

    @Override
    public void createOperator(RequestUserDto dto, String department) throws UserAlreadyExistsException {
        validateUserRegistration(dto);
        Operator operator = new Operator(dto.getUsername(), dto.getEmail(), department);
        operator.setPassword(passwordEncoder.encode(dto.getPassword()));
        operatorRepository.save(operator);
        log.info("Operator created: {}", dto.getUsername());
    }

    @Override
    public void createKitchenUser(RequestUserDto dto, String station) throws UserAlreadyExistsException {
        validateUserRegistration(dto);
        KitchenUser kitchenUser = new KitchenUser(dto.getUsername(), dto.getEmail(), station);
        kitchenUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        kitchenUserRepository.save(kitchenUser);
        log.info("Kitchen user created: {}", dto.getUsername());
    }

    @Override
    public void updateUserStatus(String userId, boolean enabled) {
        AbstractFoodopiaUser user = findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        user.setEnabled(enabled);
        saveUser(user);
        log.info("User status updated for {}: enabled = {}", userId, enabled);
    }

    @Override
    public void saveUser(AbstractFoodopiaUser user) {
        switch (user.getRole()) {
            case CUSTOMER -> customerRepository.save((Customer) user);
            case ADMIN -> administratorRepository.save((Administrator) user);
            case OPERATOR -> operatorRepository.save((Operator) user);
            case KITCHEN -> kitchenUserRepository.save((KitchenUser) user);
        }
    }

    @Override
    public UserInfoResponse buildUserInfoResponse(AbstractFoodopiaUser user) {
        UserInfoResponse.UserInfoResponseBuilder builder = UserInfoResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired());

        // Add role-specific information
        switch (user.getRole()) {
            case ADMIN -> {
                Administrator admin = (Administrator) user;
                builder.adminLevel(admin.getAdminLevel());
            }
            case KITCHEN -> {
                KitchenUser kitchen = (KitchenUser) user;
                builder.station(kitchen.getStation());
            }
            case OPERATOR -> {
                Operator operator = (Operator) user;
                builder.department(operator.getDepartment())
                        .permissions(operator.getPermissions());
            }
        }

        return builder.build();
    }

    @Override
    public void updatePassword(String userId, String newPassword) {
        AbstractFoodopiaUser user = findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        saveUser(user);
        log.info("Password updated for user: {}", userId);
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Helper method
    private void validateUserRegistration(RequestUserDto dto) throws UserAlreadyExistsException {
        if (!isUsernameAvailable(dto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + dto.getUsername());
        }
        if (!isEmailAvailable(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + dto.getEmail());
        }
    }
}