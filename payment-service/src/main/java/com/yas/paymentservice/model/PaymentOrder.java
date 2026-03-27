package com.yas.paymentservice.model;

import com.yas.paymentservice.domain.PaymentMethod;
import com.yas.paymentservice.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private PaymentOrderStatus status=PaymentOrderStatus.PENDING;

    @Column(nullable=false)
    private PaymentMethod paymentMethod;

    @Column(nullable=false)
    private String paymentLinkedId;

    @Column(nullable=false)
    private Long userId;

    @Column(nullable=false)
    private Long bookId;

    @Column(nullable=false)
    private Long salonId;


}
