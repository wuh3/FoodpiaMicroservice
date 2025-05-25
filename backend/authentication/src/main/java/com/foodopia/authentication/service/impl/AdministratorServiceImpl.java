package com.foodopia.authentication.service.impl;

import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.entity.Administrator;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.mapper.UserMapper;
import com.foodopia.authentication.repository.AdministratorRepository;
import com.foodopia.authentication.service.AbstractUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdministratorServiceImpl extends AbstractUserService<Administrator> {
    private AdministratorRepository administratorRepository;
    @Override
    protected MongoRepository<Administrator, String> getRepository() {
        return administratorRepository;
    }

    @Override
    protected Administrator createUser(RequestUserDto user) {
        Administrator admin = UserMapper.mapToAdmin(user, new Administrator());
        Optional<Administrator> optionalAdmin = administratorRepository.findByUsername(user.getUsername());
        if (optionalAdmin.isPresent()) {
            throw new UserAlreadyExistsException("Admin already registered with given username: "
                    +user.getUsername());
        }
        administratorRepository.save(admin);
        return admin;
    }

    @Override
    protected Administrator fetchUser(String username) {
        return administratorRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("Administrator not found")
        );
    }

    @Override
    protected Administrator updateUser(RequestUserDto user) {
        Administrator admin = administratorRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new UserNotFoundException("Administrator not found")
        );
        administratorRepository.save(UserMapper.mapToAdmin(user, admin));
        return admin;
    }
}
