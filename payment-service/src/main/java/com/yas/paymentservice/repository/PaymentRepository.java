package com.yas.paymentservice.repository;

import com.yas.paymentservice.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentOrder, Long> {
    PaymentOrder findByPaymentLinkedId(String paymentLinkedId);
}
