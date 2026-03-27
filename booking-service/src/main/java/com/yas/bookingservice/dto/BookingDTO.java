package com.yas.bookingservice.dto;

import com.yas.bookingservice.domain.BookingStatus;
import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
@Data
public class BookingDTO {
    private Long id;

    private Long salonId;

    private Long customerId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ElementCollection
    private Set<Long> serviceIds;

    private BookingStatus status = BookingStatus.PENDING;

    private int totalPrice;
}
