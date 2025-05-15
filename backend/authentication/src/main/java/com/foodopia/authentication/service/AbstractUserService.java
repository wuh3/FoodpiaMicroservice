package com.foodopia.authentication.service;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public abstract class AbstractUserService<T extends AbstractFoodopiaUser> {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected abstract MongoRepository<T, String> getRepository();

    protected abstract T createUser(RequestUserDto user);
    protected abstract T fetchUser(String username);
    protected abstract T updateUser(RequestUserDto user);

    public T updatePassword(String userId, String newPassword) {
        T user = getRepository().findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        return getRepository().save(user);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
