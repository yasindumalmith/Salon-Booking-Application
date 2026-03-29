package com.yas.userservice.payload.response.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private boolean enabled;
    private List<Credential> credentials=new ArrayList<>();
}
