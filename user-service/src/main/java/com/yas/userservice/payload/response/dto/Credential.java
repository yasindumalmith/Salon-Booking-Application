package com.yas.userservice.payload.response.dto;

import lombok.Data;

@Data
public class Credential {
    private String type;
    private String value;
    private boolean temporary;
}
