package com.yas.bookingservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SlotDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
