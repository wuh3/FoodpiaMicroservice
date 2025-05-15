package com.foodopia.authentication.service.impl;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.entity.Customer;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.mapper.UserMapper;
import com.foodopia.authentication.repository.CustomerRepository;
import com.foodopia.authentication.service.AbstractUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl extends AbstractUserService<Customer> {
    private CustomerRepository customerRepository;

    @Override
    protected MongoRepository<Customer, String> getRepository() {
        return customerRepository;
    }

    @Override
    protected Customer createUser(RequestUserDto user) {
        Customer customer = UserMapper.mapToCustomer(user, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(user.getUsername());
        if(optionalCustomer.isPresent()) {
            throw new UserAlreadyExistsException("Customer already registered with given mobileNumber "
                    +user.getUsername());
        }
        return customerRepository.save(customer);
    }

    @Override
    protected Customer fetchUser(String username) {
        return customerRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );
    }

    @Override
    protected Customer updateUser(RequestUserDto user) {
        Customer customer = customerRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );
        customerRepository.save(UserMapper.mapToCustomer(user, customer));
        return customer;
    }
}
