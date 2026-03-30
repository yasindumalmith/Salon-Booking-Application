package com.yas.userservice.mapper;

import com.yas.userservice.model.User;
import com.yas.userservice.payload.response.dto.UserDTO;

public class UserMapper {
    public static UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getEmail());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }
}
