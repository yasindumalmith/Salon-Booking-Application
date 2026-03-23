package com.yas.userservice.payload.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class ExceptionResponse {
    private String message;
    private String error;
    private LocalDateTime timestamp;
}
