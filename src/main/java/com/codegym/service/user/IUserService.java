package com.codegym.service.user;

import com.codegym.model.dto.UserProfileDTO;
import com.codegym.model.User;
import com.codegym.service.IGenerateService;

import java.util.Optional;

public interface IUserService extends IGenerateService<User> {
    Optional<UserProfileDTO> getUserProfile(String userName);

    Optional<User> findByUsername(String username);

    void validateUsername(String username);

    void validatePhone(String phone);
}
