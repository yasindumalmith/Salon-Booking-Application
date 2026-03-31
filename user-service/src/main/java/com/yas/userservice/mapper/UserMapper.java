package com.yas.userservice.mapper;

import com.yas.userservice.model.User;
import com.yas.userservice.payload.response.dto.UserDTO;

public class UserMapper {
    public static UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}
