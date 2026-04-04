package com.yas.paymentservice.service;

import com.stripe.exception.StripeException;
import com.yas.paymentservice.domain.PaymentMethod;
import com.yas.paymentservice.model.PaymentOrder;
import com.yas.paymentservice.payload.dto.BookingDTO;
import com.yas.paymentservice.payload.dto.UserDTO;
import com.yas.paymentservice.payload.response.PaymentLinkResponse;

public interface PaymentService {
    PaymentLinkResponse createOrder(UserDTO userDTO, BookingDTO bookingDTO, PaymentMethod paymentMethod) throws StripeException;
    PaymentOrder getPaymentOrderById(Long id) throws Exception;
    PaymentOrder getPaymentOrderByPaymentId(String paymentId);
    PaymentLinkResponse createStripePaymentLink(UserDTO userDTO, Long amount, Long orderId) throws StripeException;
    PaymentOrder proceedPayment(PaymentOrder paymentOrder, String paymentLinkId);

}
