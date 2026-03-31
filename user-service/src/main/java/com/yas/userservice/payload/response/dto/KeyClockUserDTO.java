package com.yas.userservice.payload.response.dto;

import lombok.Data;

@Data
public class KeyClockUserDTO {
    private String id;          // ✅ VERY IMPORTANT
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;

}
