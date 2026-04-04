package com.yas.paymentservice.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEventDTO {
    private String paymentId;
    private Long bookingId;
    private String status;
}
