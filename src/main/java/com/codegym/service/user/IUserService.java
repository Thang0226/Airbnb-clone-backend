package com.codegym.service.user;

import com.codegym.model.dto.UserInfoDTO;
import com.codegym.model.User;
import com.codegym.model.dto.UserProfileDTO;
import com.codegym.service.IGenerateService;

import java.util.Optional;

public interface IUserService extends IGenerateService<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void validateUsername(String username);

    void validatePhone(String phone);

    boolean validateEmail(String email);

    Iterable<UserInfoDTO> getAllUsersInfo();

    UserProfileDTO getUserProfile(String userName);
}
