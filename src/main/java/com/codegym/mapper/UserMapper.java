package com.codegym.mapper;

import com.codegym.model.User;
import com.codegym.model.dto.UserInfoDTO;
import com.codegym.model.dto.UserProfileDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserMapper {
    public static UserInfoDTO toUserInfoDTO(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setPhone(user.getPhone());
        userInfoDTO.setStatus(user.getStatus());
        return userInfoDTO;
    }

    public static List<UserInfoDTO> toUserInfoDTOList(Iterable<User> users) {
        return StreamSupport.stream(users.spliterator(), false)
                .map(UserMapper::toUserInfoDTO)
                .collect(Collectors.toList());
    }

    public static UserProfileDTO toUserProfileDTO(User user) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setAvatar(user.getAvatar());
        userProfileDTO.setFullName(user.getFullName());
        userProfileDTO.setAddress(user.getAddress());
        userProfileDTO.setPhone(user.getPhone());
        return userProfileDTO;
    }
}
