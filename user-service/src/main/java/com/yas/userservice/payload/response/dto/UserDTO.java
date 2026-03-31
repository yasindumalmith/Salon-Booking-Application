package com.yas.userservice.payload.response.dto;

import com.yas.userservice.domain.UserRole;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String fullname;
    private String username;
    private String email;
    private String phone;
    private UserRole role;
}
