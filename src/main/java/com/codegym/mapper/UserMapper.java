package com.codegym.mapper;

import com.codegym.model.User;
import com.codegym.model.dto.user.UserInfoDTO;
import com.codegym.model.dto.user.UserProfileDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserInfoDTO toUserInfoDTO(User user);

    UserProfileDTO toUserProfileDTO(User user);
}
