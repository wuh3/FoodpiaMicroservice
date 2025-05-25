package com.foodopia.authentication.service.impl;

import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.entity.Operator;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.mapper.UserMapper;
import com.foodopia.authentication.repository.OperatorRepository;
import com.foodopia.authentication.service.AbstractUserService;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public class OperatorServiceImpl extends AbstractUserService<Operator> {
    private OperatorRepository operatorRepository;
    @Override
    protected MongoRepository<Operator, String> getRepository() {
        return operatorRepository;
    }

    @Override
    protected Operator createUser(RequestUserDto user) {
        Operator operator = UserMapper.mapToOperator(user, new Operator());
        Optional<Operator> optionalOperator = operatorRepository.findByUsername(user.getUsername());
        if(optionalOperator.isPresent()) {
            throw new UserAlreadyExistsException("Operator already registered with given username: " + user.getUsername());
        }
        return operatorRepository.save(operator);
    }

    @Override
    protected Operator fetchUser(String username) {
        return operatorRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("Operator not found")
        );
    }

    @Override
    protected Operator updateUser(RequestUserDto user) {
        Operator operator = operatorRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new UserNotFoundException("Operator not found")
        );
        operatorRepository.save(UserMapper.mapToOperator(user, operator));
        return operator;
    }
}
