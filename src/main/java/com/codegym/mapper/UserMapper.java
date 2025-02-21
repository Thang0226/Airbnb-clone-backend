package com.codegym.mapper;

import com.codegym.model.User;
import com.codegym.model.dto.UserInfoDTO;
import com.codegym.model.dto.UserProfileDTO;
import org.springframework.data.domain.Page;

public class UserMapper {
    public static UserInfoDTO toUserInfoDTO(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setAvatar(user.getAvatar());
        userInfoDTO.setFullName(user.getFullName());
        userInfoDTO.setPhone(user.getPhone());
        userInfoDTO.setStatus(user.getStatus());
        return userInfoDTO;
    }

    public static Page<UserInfoDTO> toUserInfoDTOList(Page<User> users) {
        return users.map(UserMapper::toUserInfoDTO);
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
