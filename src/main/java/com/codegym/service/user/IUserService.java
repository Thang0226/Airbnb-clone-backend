package com.codegym.service.user;

import com.codegym.model.DTO.UserProfileDTO;
import com.codegym.model.User;
import com.codegym.service.IGenerateService;

import java.util.Optional;

public interface IUserService extends IGenerateService<User> {
    Optional<UserProfileDTO> getUserProfile(String userName);

    Optional<User> findByUsername(String username);
}
