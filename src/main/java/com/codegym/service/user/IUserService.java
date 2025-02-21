package com.codegym.service.user;

import com.codegym.model.dto.UserInfoDTO;
import com.codegym.model.User;
import com.codegym.model.dto.UserProfileDTO;
import com.codegym.service.IGenerateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IUserService extends IGenerateService<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void validateUsername(String username);

    void validatePhone(String phone);

    boolean validateEmail(String email);

    UserProfileDTO getUserProfile(String userName);
  
    Page<UserInfoDTO> getAllUsersInfo(Pageable pageable);

    UserInfoDTO getUserInfo(Long userId);
}
